from fastapi import FastAPI, HTTPException, BackgroundTasks
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from langchain_community.llms import OpenAI
import os
import requests
from dotenv import load_dotenv
import pandas as pd
from sqlalchemy import create_engine
from sklearn.preprocessing import StandardScaler, OneHotEncoder
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.cluster import KMeans
import numpy as np
from apscheduler.schedulers.background import BackgroundScheduler

# Load environment variables
load_dotenv()

app = FastAPI()

# CORS settings
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# OpenAI API key
api_key = os.getenv("OPENAI_API_KEY")
if api_key is None:
    raise ValueError("OPENAI_API_KEY environment variable is not set.")

# OpenAI model initialization
llm = OpenAI(api_key=api_key)

# Load CSV data
csv_path = os.getenv("CSV_PATH", "woori_bank_savings.csv")
woori_bank_savings = pd.read_csv(csv_path).to_dict(orient='records')

# Store user asset information and responses
user_asset_info = {}
user_responses = {}
# 알림 저장소
notifications = []

# 환율 API URL
API_URL = f"https://api.exchangerate-api.com/v4/latest/USD?apikey={os.getenv('EXCHANGE_API_KEY')}"


# 환율 관련 저장된 사용자 설정
user_rates = []

# Predefined questions
questions = [
    "예금 목적은 무엇인가요?",
    "적금 목표 금액은 얼마인가요?",
    "펀드에 대해 어떤 목표를 가지고 계신가요?"
]

class UserRequest(BaseModel):
    question: str

class AssetInfo(BaseModel):
    deposit: str
    savings: str
    fund: str
    debt: str  # 부채 추가

class ResponseInfo(BaseModel):
    user_id: str
    response: str

class UserId(BaseModel):
    user_Id: str

# 환율 관련 요청 모델
class UserRateRequest(BaseModel):
    currency: str  # 목표 환율 설정(엔, 달러, 유로 등)
    target_rate: float  # 목표 환율
    action: str  # "buy" 또는 "sell"
    amount: float  # 목표 금액


@app.post("/set_asset_info")
def set_asset_info(asset_info: AssetInfo):
    global user_asset_info
    
    def clean_and_convert(value: str):
        """쉼표를 제거하고, 숫자로 변환하는 함수"""
        try:
            return int(value.replace(",", "")) if value != "정보 없음" else "정보 없음"
        except ValueError:
            return "정보 없음"  # 변환이 불가능한 값에 대해서는 "정보 없음" 반환

    # 자산 정보를 10000배 곱한 뒤 저장 (소수점 처리 및 천 단위 구분)
    deposit = clean_and_convert(asset_info.deposit)
    savings = clean_and_convert(asset_info.savings)
    fund = clean_and_convert(asset_info.fund)
    debt = clean_and_convert(asset_info.debt)

    # 총 자산 계산 (예금 + 적금 + 펀드 + 부채)
    total_assets = deposit + savings + fund + debt

    user_asset_info = {
        "deposit": str(deposit) if deposit != "정보 없음" else "정보 없음",
        "savings": str(savings) if savings != "정보 없음" else "정보 없음",
        "fund": str(fund) if fund != "정보 없음" else "정보 없음",
        "debt": str(debt) if debt != "정보 없음" else "정보 없음",
        "totalAssets": str(total_assets) if total_assets != "정보 없음" else "정보 없음"  # 총 자산 추가
    }

    # 자산 정보를 챗봇에 학습시키기
    sendAssetInfoToChatbot(user_asset_info)
    
    return {"status": "success", "message": "User asset information updated successfully"}

def sendAssetInfoToChatbot(asset_info: dict):
    """자산 정보를 챗봇에 전달하는 메서드"""
    print(f"Sending asset info to chatbot: {asset_info}")  # 실제로는 Langchain 모델에 전달

@app.post("/recommend_savings")
def recommend_savings(request: UserRequest):
    try:
        question = request.question.strip()

        # Convert saving products to a single string
        product_info_str = "\n".join([
            f"{item['상품명']}: 금리 {item['금리']}%, 조건 {item['조건']}, 혜택 {item['혜택']}"
            for item in woori_bank_savings
        ])

        # Include asset info in the prompt if available
        if user_asset_info:
            asset_info_str = (
                f"사용자의 자산 내역은 다음과 같습니다 - 예금: {user_asset_info['deposit']}원, "
                f"적금: {user_asset_info['savings']}원, 펀드: {user_asset_info['fund']}원. "
                f"부채: {user_asset_info['debt']}원, 총 자산: {user_asset_info['totalAssets']}원"
            )
        else:
            asset_info_str = ""

        # Identify if the question is finance-related
        keywords = ["은행", "적금", "금리", "상품", "돈", "이자", "자산", "예금"]
        is_financial_question = any(keyword in question.lower() for keyword in keywords)

        if is_financial_question:
            prompt = (
                f"{asset_info_str} 너는 금융 에이전트야 질문에 대해 100자 미만으로 답변해주고 문장은 무조건 마무리해줘 "
                f"답변은 본론부터 시작하고, 금융 상품 정보는 다음과 같아: "
                f"{product_info_str}. 질문: {question}?"
            )
            response = llm(prompt)
             # '\n' 제거
            response = response.replace("\n", " ")  # 줄 바꿈 제거하고 공백으로 대체
        else:
            response = "죄송합니다. 은행 및 금융 관련 질문에만 답변을 제공할 수 있습니다."

        return {"response": response}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error: {str(e)}")

@app.post("/analyze-user")
def analyze_user(user: UserId):
    # Database connection
    engine = create_engine('mysql+pymysql://gunwoo2:woorifisa3!W@118.67.131.22:3306/gunwoo')
    query = "SELECT * FROM user_profile"
    data = pd.read_sql(query, engine)

    if data.empty:
        raise HTTPException(status_code=404, detail="No profile data found.")
    
    # Preprocess data
    data = data.drop(['password', 'cluster'], axis=1, errors='ignore')
    data = data.replace('', np.nan).fillna(0)

    categorical_features = ['gender', 'employment_status']
    numerical_features = [col for col in data.columns if col not in categorical_features + ['user_id']]

    preprocessor = ColumnTransformer([
        ('num', StandardScaler(), numerical_features),
        ('cat', OneHotEncoder(drop='first'), categorical_features)
    ])
    pipeline = Pipeline(steps=[('preprocessor', preprocessor)])
    data_processed = pipeline.fit_transform(data)

    n_clusters = min(4, len(data_processed))
    kmeans = KMeans(n_clusters=n_clusters, random_state=42)
    clusters = kmeans.fit_predict(data_processed)
    data['cluster'] = clusters

    user_data = data[data['user_id'] == user.user_Id]
    user_processed = pipeline.transform(user_data.drop(['user_id'], axis=1))
    user_cluster = kmeans.predict(user_processed)[0]

    # 클러스터별 평균 순자산 및 부채 계산
    cluster_data = data[data['cluster'] == user_cluster]
    avg_net_assets = cluster_data['total_financial_assets'].mean() - cluster_data['debt_amount'].mean()
    avg_debt = cluster_data['debt_amount'].mean()
    user_net_assets = user_data['total_financial_assets'].values[0] - user_data['debt_amount'].values[0]
    user_debt = user_data['debt_amount'].values[0]

    return {
        "userType": f"Cluster {user_cluster}",
        "userNetAssets": user_net_assets,
        "userDebt": user_debt,
        "avgNetAssets": avg_net_assets,
        "avgDebt": avg_debt
    }

from decimal import Decimal

@app.get("/get_exchange_rate")
def get_exchange_rate():
    try:
        # API 호출
        response = requests.get(API_URL)
        data = response.json()

        if response.status_code != 200:
            raise HTTPException(status_code=500, detail="환율 정보를 가져오는 데 실패했습니다.")

        rates = data["rates"]

        # 기준 환율 (1 USD -> KRW)
        usd_to_krw = rates.get("KRW")
        if not usd_to_krw:
            raise HTTPException(status_code=500, detail="KRW 환율 정보를 가져올 수 없습니다.")

        # USD를 기준으로 다른 통화 계산
        converted_rates = {
            "USD": round(usd_to_krw, 2),  # 1 USD = KRW
            "EUR": round(usd_to_krw / rates["EUR"], 2),  # 1 EUR = KRW
            "JPY": round(usd_to_krw / rates["JPY"]*100, 2)   # 1 JPY = KRW
        }

        return {"rates": converted_rates}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"오류 발생: {str(e)}")



@app.post("/set_target_rate")
def set_target_rate(user_rate: UserRateRequest, background_tasks: BackgroundTasks):
    try:
        if user_rate.target_rate <= 0 or user_rate.amount <= 0:
            raise HTTPException(status_code=400, detail="목표 환율과 금액은 0보다 커야 합니다.")
        
        if user_rate.currency not in ["USD", "EUR", "JPY"]:
            raise HTTPException(status_code=400, detail="지원하지 않는 통화입니다.")

        user_rates.append(user_rate)
        background_tasks.add_task(check_target_rate, user_rate)
        return {"message": "목표 환율이 성공적으로 설정되었습니다."}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"설정 중 오류 발생: {str(e)}")


def check_target_rate(user_rate: UserRateRequest):
    try:
        response = requests.get(API_URL)
        data = response.json()

        if response.status_code != 200:
            raise Exception("환율 정보를 가져오는 데 실패했습니다.")

        rates = data["rates"]
        krw_rate = rates.get("KRW")
        if not krw_rate or krw_rate == 0:
            raise Exception("유효한 KRW 환율 정보를 가져올 수 없습니다.")

        current_rate = rates.get(user_rate.currency)
        if current_rate:
            # 현재 환율을 KRW 기준으로 변환
            converted_rate = current_rate * krw_rate

            # 구매일 경우: 목표 환율보다 떨어지면 알림
            if user_rate.action == "buy" and converted_rate <= user_rate.target_rate:
                notify_user(user_rate, converted_rate)

            # 판매일 경우: 목표 환율보다 오르면 알림
            elif user_rate.action == "sell" and converted_rate >= user_rate.target_rate:
                notify_user(user_rate, converted_rate)
        else:
            raise Exception("목표 환율에 해당하는 정보를 찾을 수 없습니다.")
    except Exception as e:
        print(f"Error while checking target rate: {str(e)}")



def notify_user(user_rate, current_rate):
    try:
        notification = {
            "userId": user_rate.user_id,
            "message": f"목표 환율 {user_rate.target_rate}에 거래 완료하였습니다! 현재 환율: {current_rate:.2f}",
        }
        response = requests.post("http://localhost:8080/notifications/create", json=notification)
        response.raise_for_status()  # 요청 실패 시 예외 발생
    except requests.exceptions.RequestException as e:
        print(f"Failed to send notification to Spring Boot: {e}")


# APScheduler 설정
scheduler = BackgroundScheduler()

def monitor_exchange_rates():
    try:
        for user_rate in user_rates:
            check_target_rate(user_rate)
    except Exception as e:
        print(f"Monitor Error: {e}")

scheduler.add_job(monitor_exchange_rates, "interval", hours=24)  # 24시간마다 실행
scheduler.start()

@app.on_event("shutdown")
def shutdown_event():
    scheduler.shutdown()


@app.post("/notifications")
def create_notification(notification: dict):
    """
    알림 데이터를 저장하는 엔드포인트.
    고유 ID를 생성하여 알림에 추가합니다.
    """
    notification_id = len(notifications) + 1  # 고유 ID 생성
    notification["id"] = notification_id
    notification["read"] = False  # 읽음 상태 초기화
    notifications.append(notification)
    print("알림 저장:", notification)
    return {"status": "success", "data": notification}


@app.get("/notifications/{user_id}")
def get_notifications(user_id: str):
    """
    특정 사용자의 알림 목록을 반환하는 엔드포인트.
    """
    user_notifications = [n for n in notifications if n["userId"] == user_id]
    return {"status": "success", "data": user_notifications}

@app.post("/notifications/mark-as-read/{notification_id}")
def mark_as_read(notification_id: int):
    """
    특정 알림을 읽음 처리.
    """
    for notification in notifications:
        if notification["id"] == notification_id:
            notification["read"] = True
            print("알림 읽음 처리:", notification)
            return {"status": "success", "data": notification}
    raise HTTPException(status_code=404, detail="Notification not found")

@app.delete("/notifications/delete/{notification_id}")
def delete_notification(notification_id: int):
    """
    특정 알림을 삭제하는 엔드포인트.
    """
    global notifications
    before_count = len(notifications)
    notifications = [n for n in notifications if n["id"] != notification_id]
    after_count = len(notifications)

    if before_count == after_count:
        raise HTTPException(status_code=404, detail="Notification not found")

    print(f"알림 삭제: ID {notification_id}")
    return {"status": "success", "message": "Notification deleted successfully"}