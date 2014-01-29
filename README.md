bkbc-msglove
============

## 說明

腦殘錢包分享版，用來利用LINE或是電話通信機制來發比特幣，有四個關鍵字分別用（-L-/-O-/-V-/-E-）代替。

### app 列表

App1 = bkbc-msglove @ PS

App2 = bitcoin_uri_accept_app @ PS 

App3 = bkbc-msglove @ PR

App4 = [Bitcoin Wallet - Google Play Android 應用程式](https://play.google.com/store/apps/details?id=de.schildbach.wallet) @ PR

### 付出端

1. App1 修改預設內容內含四個關鍵字分別用（-L-/-O-/-V-/-E-）代替，（-L-祝-O-台中2014-V-快樂-E-年）成為腦殘密碼轉實際密鑰
2. App1 根據輸入得出地址並轉發支付請求請求
3. App2 付款給新增的腦殘錢包地址
4. App1 分享給 Line/Email/Facebook 例如（-L-祝-O-台中2014-V-快樂-E-年)，另外電話語音通知四個關鍵字。


### 接收端

1. 接收程式例如 Line 長按公開給 App3，如是語音資訊可直接開啟 App3 輸入
2. App3 將密鑰轉格式放到 sdcard/Download 供 App4 讀取
3. 開啟 App4 - 設定 - back up keys - Restore private keys - msglove-yyyyMMdd.key - reset
4. 重啟 App4 可以確認該地址已經匯入。
 

## 截圖

[write key file to sdcard 寫入密鑰檔 · Issue #2 · y12studio/bkbc-msglove](https://github.com/y12studio/bkbc-msglove/issues/2)


## 安裝

目前只有提供下載安裝，並無商店版本。[Releases · y12studio/bkbc-msglove](https://github.com/y12studio/bkbc-msglove/releases)
