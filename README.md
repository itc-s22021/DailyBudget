# デイリーバジェット (Daily Budget)
## 概要
「デイリーバジェット」は、給料日から次の給料日まで1日に使える金額を均等に割り振り、カレンダー上で確認できるAndroidアプリです。
## 機能
- 月の支出予定金額を設定
- 給料日を設定し、土日・祝日の場合の前払い・後払いを選択
- 支出記録（音声入力・手動入力）
- 支出予定金額と実際の支出を比較し、超過・不足を色分け表示
- 支出額の再分配機能
## 環境・技術スタック
- 言語: Kotlin
- データ保存: Room, SharedPreferences
- UI: RecyclerView, Material Design
- その他: Firebase（認証用）
## インストール方法
1. このリポジトリをクローン
## **6. 使い方（Usage）**
1. 初回起動時に給料日と支出予定金額を設定  
2. カレンダー画面で支出額を記録  
3. 記録した金額に応じて予算が再分配される
## ライセンス
MIT License

## 環境情報
- OS:　Linux
- 開発ツール: Android Studio
- 言語: Kotlin
- Gradle: 8.x
- ビルドツール: Android Gradle Plugin（AGP）8.x
- データベース: Room, SharedPreferences
- バージョン管理: Git
