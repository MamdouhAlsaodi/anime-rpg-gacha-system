# 🎮 نظام Anime RPG Gacha System

> مشروع Java OOP كامل لفكرة Gacha / Loot Box مستوحاة من ألعاب الأنمي، مع Client-Server Architecture وواجهة Swing وتحسينات عرض ممتازة للمناقشة الجامعية.

## 🌐 اللغات المتاحة

المشروع متوفر بثلاث لغات:

- العربية — هذا الملف.
- البرتغالية — `README-pt.md`.
- الإنجليزية — `README.md`.

الكود نفسه مكتوب بالإنجليزية لأن هذا هو الأسلوب الأفضل في Java، لكن الشرح والتسليم يمكن تقديمهما بالعربية أو البرتغالية أو الإنجليزية.

---

## 🏗️ الهيكلية ولماذا؟

المشروع يستخدم **3 طبقات**:

1. **Client / Presentation Layer**
   - تحتوي على Swing UI مثل `MainScreen`, `SummonScreen`, `InventoryScreen`, `PresentationScreen`, و `GamePanel`.
   - مهمتها عرض اللعبة واستقبال أفعال المستخدم فقط.

2. **API / Protocol Layer**
   - تحتوي على `CommandCode`, `GameRequest`, `GameResponse`, `CommandHandler`, و `CommandRouter`.
   - تعمل كجسر واضح بين الواجهة والسيرفر.

3. **Server / Business Layer**
   - تحتوي على `GameEngine`, models, services, factories, exceptions.
   - هنا توجد قواعد اللعبة: gems, summon rates, pity, inventory, missions, upgrades, rewards.

### لماذا هذه الهيكلية؟

- فصل واضح بين الواجهة والمنطق وقناة الاتصال.
- سهولة الاختبار من `Main.java` بدون فتح Swing.
- سهولة إضافة أوامر جديدة أو واجهة جديدة في المستقبل.
- توضيح ممتاز لمفاهيم OOP: abstraction, inheritance, polymorphism, encapsulation, factory method, custom exceptions.

---

## 🔌 توثيق API باختصار

العميل يرسل `GameRequest` إلى السيرفر عبر TCP Socket، ثم `CommandRouter` يختار handler المناسب، وبعدها يرجع السيرفر `GameResponse`.

### Commands

- `SUMMON_SINGLE` — تكلفة 160 gems، سحبة واحدة.
- `SUMMON_TEN` — تكلفة 1600 gems، عشر سحبات.
- `VIEW_INVENTORY` — عرض inventory.
- `VIEW_PLAYER` — عرض بيانات اللاعب والإحصائيات.
- `LEVEL_UP` — ترقية شخصية.
- `ENHANCE_ITEM` — تحسين item.
- `HELP` — عرض الأوامر.
- `EXIT` — إنهاء الاتصال.

### Flow

```text
Client UI → GameRequest → ServerConnector → GachaGameServer → CommandRouter → Handler → GameEngine → GameResponse
```

---

## 📺 طبقة العرض والتقرير

تم تحسين `PresentationScreen.java` لكي يصبح المشروع جاهزاً للعرض أمام الزملاء:

- عرض مقدمة المشروع.
- شرح 3-layer architecture.
- شرح gameplay loop: summon → loadout → fight → earn → upgrade.
- عرض خمس مراحل مرسومة بـ `Graphics2D`.
- عرض OOP highlights.
- شرح gacha rates و pity و rewards.
- مسار demo واضح للمناقشة.

---

## ▶️ التشغيل

```bash
find src -name "*.java" > sources.txt
javac -d out @sources.txt
```

### حسابات التجربة

```bash
# حساب عادي: يبدأ بجواهر عادية
printf 'player1\n' | java -cp out Main

# حساب VIP: فيه جواهر كثيرة للتجربة والعرض
printf 'mamdouh\n' | java -cp out Main
```

لتشغيل الواجهة:

```bash
java -cp out server.GachaGameServer
java -cp out client.GachaClientApp
```

عند ظهور نافذة الاسم في الواجهة:

- اكتب `player1` لتجربة حساب عادي.
- اكتب `mamdouh` لتجربة حساب فيه جواهر كثيرة.

---

## 🗺️ الخطة

الخطة الرئيسية موجودة في:

- `plan.md` — ملخص خطة v0.0 و v0.1 ومكان تحديثات المستقبل.
- `PLAN_v0.0.md` — الخطة الأصلية.
- `PLAN_v0.1.md` — خطة التحسين.

