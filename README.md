# 📚 Library Management System

Sistem për menaxhimin e bibliotekës, i zhvilluar në **Java** me qasjen **Test Driven Development (TDD)**.

---

## 📖 Përshkrimi i projektit

Shumë biblioteka të vogla, si ato të shkollave, fakulteteve apo komunave, e mbajnë ende evidencën
e librave në fletore ose në tabela Excel. Kjo qasje ka disa probleme:

- **Gabime në evidencë**: i njëjti libër regjistrohet dy herë, ose mungojnë të dhëna si ISBN apo titulli.
- **Mungesë informacioni**: bibliotekari nuk e di menjëherë nëse një libër është i lirë apo i huazuar.
- **Libra të humbur**: nuk dihet saktë kush e ka huazuar një libër dhe nëse është kthyer.
- **Humbje kohe**: kërkimi manual i librave dhe i huazimeve zgjat shumë.

**Library Management System** i zgjidh këto probleme me një sistem të centralizuar, ku çdo libër
regjistrohet me të dhëna të validuara dhe çdo huazim e kthim ruhet automatikisht.

---

## 🎯 Qëllimi kryesor

Qëllimi kryesor është **digjitalizimi dhe automatizimi i proceseve bazë të një biblioteke**:
regjistrimi i librave, huazimi dhe kthimi i tyre. Kështu evidenca bëhet e saktë, e shpejtë dhe e besueshme.

Aplikacioni zhvillohet me **TDD**: për çdo funksionalitet shkruhet së pari testi (RED), pastaj
kodi minimal që e kalon testin (GREEN), dhe në fund kodi përmirësohet pa e ndryshuar sjelljen (REFACTOR).

---

## ✨ Funksionalitetet

| # | Funksionaliteti | Përshkrimi | Statusi |
|---|-----------------|------------|---------|
| 1 | Shtimi i librit | Regjistrimi i librit me ISBN, titull dhe autor. Refuzohen librat pa ISBN, pa titull ose me ISBN të dyfishtë. | ✅ Implementuar (TDD) |
| 2 | Huazimi i librit | Një anëtar huazon një libër të lirë. Refuzohen librat që nuk ekzistojnë, librat e huazuar tashmë dhe anëtarët pa ID. | ✅ Implementuar (TDD) |
| 3 | Kthimi i librit | Libri i huazuar kthehet dhe bëhet sërish i lirë. Refuzohen librat që nuk janë huazuar ose nuk ekzistojnë. | ✅ Implementuar (TDD) |
| 4 | Kërkimi i librave | Kërkimi sipas titullit ose autorit. | 🔜 Planifikuar |
| 5 | Menaxhimi i anëtarëve | Regjistrimi dhe menaxhimi i anëtarëve të bibliotekës. | 🔜 Planifikuar |
| 6 | Afatet dhe vonesat | Afati i kthimit dhe llogaritja e vonesave. | 🔜 Planifikuar |

---

## 🛠️ Teknologjitë

| Teknologjia | Përdorimi |
|-------------|-----------|
| **Java 17** | Gjuha kryesore e programimit |
| **Maven** | Menaxhimi i projektit, i varësive dhe ekzekutimi i testeve |
| **JUnit 5 (Jupiter)** | Shkrimi dhe ekzekutimi i testeve të njësisë |
| **Git & GitHub** | Kontrolli i versioneve dhe dëshmia e cikleve TDD përmes commit-eve |
| **VS Code / IntelliJ IDEA** | Mjedisi i zhvillimit |

---

## 📁 Struktura e projektit

```
library-management-system
├── pom.xml
├── README.md
└── src
    ├── main/java/com/library
    │   ├── Book.java            # Modeli i librit
    │   └── Library.java         # Logjika e bibliotekës
    └── test/java/com/library
        ├── AddBookTest.java     # Testet për shtimin e librit
        ├── BorrowBookTest.java  # Testet për huazimin
        └── ReturnBookTest.java  # Testet për kthimin
```

---

## ▶️ Si të ekzekutohet

Kërkesat: **Java 17+** dhe **Maven 3.8+**.

```bash
git clone https://github.com/SaraBajraktari1/library-managment-system.git
cd library-managment-system
mvn test
```

Rezultati i pritur:

```
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 🔁 Procesi TDD (Red → Green → Refactor)

Historiku i commit-eve tregon tre cikle të plota TDD:

| Cikli | 🔴 RED | 🟢 GREEN | 🔵 REFACTOR |
|-------|--------|----------|-------------|
| 1. Shtimi i librit | Testet për `addBook` dështojnë | Implementimi i `addBook` | Nxjerrja e metodës `requireText` |
| 2. Huazimi | Testet për `borrowBook` dështojnë | Implementimi i `borrowBook` | Nxjerrja e metodës `findBookOrThrow` |
| 3. Kthimi | Testet për `returnBook` dështojnë | Implementimi i `returnBook` | Zhvendosja e kontrollit të gjendjes te `Book` |

Historikun mund ta shihni me `git log --oneline`.

---

## 👩‍💻 Autorja

**Sara Bajraktari**, Master, Semestri 3
Lënda: *Zhvillimi i Drejtuar nga Testimi*