# Вейп Калькулятори — Android App

Android-додаток на Kotlin по мотивам [vape-calculator.netlify.app](https://vape-calculator.netlify.app/)

**Автор оригіналу:** BMWeast (@BMWeast523)

---

## 📱 Калькулятори

1. **Калькулятор вейп-рідини** — розрахунок об'ємів VG/PG/нікотину та ароматизаторів
2. **Калькулятор змішування основ** — змішування двох баз з різним нікотином та VG/PG
3. **Підвищення/зниження концентрації** — коригування відсотків ароматизаторів
4. **Флейвор шот по рецепту** — розрахунок об'ємів шоту за відсотками
5. **Загальний флейвор шот** — пошук максимального об'єму рідини для шоту
6. **Калькулятор спіралі** — розрахунок опору, потужності, довжини дроту

---

## 🚀 Як відкрити в Android Studio

### Крок 1 — Встановлення
- Завантаж [Android Studio](https://developer.android.com/studio) (безкоштовно)
- Встанови на Windows/Mac/Linux

### Крок 2 — Відкрити проєкт
1. Розпакуй цей ZIP архів
2. Відкрий Android Studio
3. `File → Open` → вибери папку `VapeCalc`
4. Зачекай поки Gradle синхронізується (1-3 хв)

### Крок 3 — Запуск на телефоні
1. На телефоні: `Налаштування → Про телефон → Номер збірки` (тапни 7 разів)
2. `Налаштування → Для розробників → Налагодження USB` (увімкни)
3. Підключи телефон кабелем до ПК
4. В Android Studio натисни ▶️ Run

### Крок 4 — Зібрати APK
`Build → Build Bundle(s) / APK(s) → Build APK(s)`
APK файл буде в: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📁 Структура проєкту

```
VapeCalc/
├── app/
│   ├── src/main/
│   │   ├── java/com/bmweast/vapecalc/
│   │   │   ├── MainActivity.kt
│   │   │   └── ui/
│   │   │       ├── LiquidFragment.kt      ← Калькулятор рідини
│   │   │       ├── BaseFragment.kt        ← Змішування основ
│   │   │       ├── BoostFragment.kt       ← Буст ароматизаторів
│   │   │       ├── ShotFragment.kt        ← Флейвор шот
│   │   │       ├── GeneralShotFragment.kt ← Загальний шот
│   │   │       └── CoilFragment.kt        ← Калькулятор спіралі
│   │   ├── res/
│   │   │   ├── layout/                    ← XML розмітки екранів
│   │   │   ├── values/                    ← Кольори, стилі, рядки
│   │   │   └── drawable/                  ← Фони кнопок та карток
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── gradle.properties
```

---

## 🎨 Дизайн

Темна кольорова схема з оригінального сайту:
- Фон: `#1a1a2e`
- Акцент: `#e94560`
- Картки: `#1e2d4a`

---

## ⚠️ Можливі помилки при відкритті

**"SDK not found"** → Android Studio → `SDK Manager` → встанови Android 14 (API 34)

**Gradle sync failed** → `File → Invalidate Caches → Restart`

**"minSdk"** помилка → в `app/build.gradle` змінити `minSdk 24` на нижче якщо потрібно.
