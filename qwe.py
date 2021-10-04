import pandas as pd
import requests
from bs4 import BeautifulSoup

df1 = pd.read_csv("한국소비자원 생필품 및 서비스 가격 정보_20210827.csv", encoding='euc-kr', index_col=0)
df1.drop(['Unnamed: 1'], axis = 1, inplace=True)
print(df1.loc[df1['판매업소'] == 'CU(본사)'])
print(df1.loc[df1['판매업소'] == 'GS25(본사)'])
print(df1.loc[df1['판매업소'] == '세븐일레븐(본사)'])
