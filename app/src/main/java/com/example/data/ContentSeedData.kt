package com.example.data

import com.example.data.database.ContentItemEntity

object ContentSeedData {
    val items = listOf(
        // 1. Guided Meditations (6 items)
        ContentItemEntity(
            id = 1,
            title = "مدیتیشن کاهش استرس و آرامش ذهن",
            category = "MEDITATION",
            shortDescription = "روی آرامش تمرکز کنید و استرس‌های روزانه خود را تخلیه نمایید.",
            durationSeconds = 300,
            iconEmoji = "🧘",
            isFree = true,
            guidedText = "یک موقعیت راحت پیدا کنید. چشم‌هایتان را ببندید.\n\nبه آرامی نفس بکشید. جریان هوا را احساس کنید که وارد بینی‌تان می‌شود و خارج می‌شود.\n\nبا هر بازدم، تصور کنید که تنش‌ها، نگرانی‌ها و خستگی‌های انباشته شده از بدنتان بیرون می‌روند.\n\nذهن شما ممکن است به جاهای دیگر سفر کند، این کاملاً طبیعی است. به آرامی و بدون سرزنش، تمرکز خود را مجدداً به نفس کشیدن بازگردانید.\n\nدر این لحظه، شما در امان هستید. هیچ کاری برای انجام دادن نیست، فقط وجود داشته باشید. آرامش را حس کنید."
        ),
        ContentItemEntity(
            id = 2,
            title = "مدیتیشن تمرکز و هشیاری حین کار",
            category = "MEDITATION",
            shortDescription = "افزایش تمرکز ذهن و بازیابی بهره‌وری برای کارهای خلاقانه.",
            durationSeconds = 180,
            iconEmoji = "💻",
            isFree = false,
            guidedText = "ستون فقرات خود را صاف نگه دارید. شانه‌ها را آزاد کنید.\n\nچند نفس عمیق و آگاهانه بکشید تا ذهن و بدن هم‌راستا شوند.\n\nتمرکز خود را روی یک نقطه متمرکز کنید - شاید احساس تماس پاهایتان با زمین یا عبور نسیم ملایم از روی پوستتان.\n\nهر فکری که مربوط به گذشته یا کارهای آینده است را به آرامی کنار بگذارید. شما اکنون اینجا هستید تا روی این لحظه تمرکز کنید.\n\nاحساس کنید قدرت ذهن و توجه شما با هر نفس تقویت می‌شود. اکنون با تمرکز کامل به کار خود بازگردید."
        ),
        ContentItemEntity(
            id = 3,
            title = "مدیتیشن خواب عمیق و آسوده",
            category = "MEDITATION",
            shortDescription = "ذهن شلوغ خود را خاموش کنید و آماده خوابی شیرین شوید.",
            durationSeconds = 600,
            iconEmoji = "🌙",
            isFree = true,
            guidedText = "روی تخت خود به پشت دراز بکشید. چشم‌های خود را آرام ببندید.\n\nیک نفس عمیق بکشید و بگذارید بدنتان در تشک غرق شود.\n\nتوجه خود را به انگشتان پا ببرید، بگذارید کاملاً شل شوند. سپس مچ پا، ساق پا، زانوها و ران‌ها.\n\nاین آرامش را به عضلات کمر، شکم، قفسه سینه، بازوها، گردن و در نهایت عضلات صورت هدایت کنید.\n\nتمام روز سپری شده را رها کنید. وظایف امروز به پایان رسیده است. شما لایق استراحت هستید. اجازه دهید خوابی عمیق و آرام شما را در بر بگیرد."
        ),
        ContentItemEntity(
            id = 4,
            title = "مدیتیشن قدردانی و عشق به خود",
            category = "MEDITATION",
            shortDescription = "پرورش احساسات مثبت، شفقت و سپاسگزاری در فضای قلب.",
            durationSeconds = 240,
            iconEmoji = "❤️",
            isFree = false,
            guidedText = "دست راست خود را روی قلبتان بگذارید. ضربان آرام آن را احساس کنید.\n\nبه خاطر زندگی، به خاطر تلاش‌هایتان و به خاطر وجود خودتان سپاسگزار باشید.\n\nذهن خود را به سه چیز یا سه شخصی معطوف کنید که صمیمانه بابت حضورشان در زندگیتان شکرگزار هستید.\n\nبگذارید این احساس گرم و دلپذیر قدردانی، کل وجود و فضای سینه شما را پر کند.\n\nبه خود بگویید: «من ارزشمند هستم. من کافی هستم. من با عشق به جلو حرکت می‌کنم.» این احساس را با خود در طول روز همراه داشته باشید."
        ),
        ContentItemEntity(
            id = 5,
            title = "مدیتیشن رهایی از اضطراب ناگهانی",
            category = "MEDITATION",
            shortDescription = "تسکین فوری تپش قلب و هراس با تنفس‌های هدایت‌شده.",
            durationSeconds = 300,
            iconEmoji = "🌸",
            isFree = true,
            guidedText = "اگر تپش قلب یا احساس اضطراب شدید دارید، دستتان را روی شکم بگذارید. شما کاملاً در امان هستید.\n\nبه آرامی و با شمارش دم بگیرید... یک، دو، سه... حالا به آرامی بازدم کنید... یک، دو، سه، چهار.\n\nاحساس کنید با هر بازدم، هورمون‌های استرس و تنش از کف پاهایتان خارج شده و وارد زمین می‌شوند.\n\nاضطراب فقط یک حس گذرا در بدن است. مانند ابر در آسمان ذهن پدیدار می‌شود و می‌رود. شما آسمان هستید، نه ابرها.\n\nاحساس امنیت و ثبات را در بدن خود بازیابی کنید. آرامش تدریجاً باز می‌گردد."
        ),
        ContentItemEntity(
            id = 6,
            title = "مدیتیشن حضور در لحظه حال (مایندفولنس)",
            category = "MEDITATION",
            shortDescription = "برقراری ارتباط مجدد با حواس پنج‌گانه و رهایی از هیاهو.",
            durationSeconds = 480,
            iconEmoji = "🍃",
            isFree = false,
            guidedText = "چشمان خود را باز نگه دارید یا ببندید. اجازه دهید صداهای محیطی بدون تفسیر وارد گوش شما شوند.\n\n۵ چیز که در اطراف می‌بینید، ۴ چیز که می‌توانید لمس کنید، ۳ صدا که می‌شنوید، ۲ رایحه که حس می‌کنید و ۱ طعم را در دهان خود بیابید.\n\nاین تمرین حواس پنج‌گانه شما را به سرعت به واقعیت فیزیکی لحظه حال پیوند می‌دهد.\n\nذهن ما مدام در گذشته یا آینده پرسه می‌زند، اما زندگی فقط در همین لحظه جریان دارد.\n\nبا کمال آرامش، از بودن در همین ثانیه لذت ببرید. شما کاملاً زنده و هشیار هستید."
        ),

        // 2. Breathing Exercises (6 items)
        ContentItemEntity(
            id = 7,
            title = "تنفس ۴-۷-۸ (آرامش عمیق)",
            category = "BREATHING",
            shortDescription = "الگوی تنفس مشهور جهانی برای کاهش سریع ضربان قلب و استراحت بدن.",
            durationSeconds = 120,
            iconEmoji = "🌬️",
            isFree = true,
            inhaleSeconds = 4,
            holdSeconds = 7,
            exhaleSeconds = 8
        ),
        ContentItemEntity(
            id = 8,
            title = "تنفس جعبه‌ای (تمرکز جنگجو)",
            category = "BREATHING",
            shortDescription = "تکنیک مورد استفاده تکاوران برای حفظ خونسردی و وضوح ذهنی در فشار بالا.",
            durationSeconds = 120,
            iconEmoji = "📦",
            isFree = false,
            inhaleSeconds = 4,
            holdSeconds = 4,
            exhaleSeconds = 4
        ),
        ContentItemEntity(
            id = 9,
            title = "تنفس آرام‌بخش سریع (۲-۲-۴)",
            category = "BREATHING",
            shortDescription = "تکنیک فوق‌العاده سریع برای متعادل کردن مجدد ریتم تنفس حین تنش روزانه.",
            durationSeconds = 90,
            iconEmoji = "⚡",
            isFree = true,
            inhaleSeconds = 2,
            holdSeconds = 2,
            exhaleSeconds = 4
        ),
        ContentItemEntity(
            id = 10,
            title = "تنفس بیداری و انرژی‌بخش (۴-۲-۲)",
            category = "BREATHING",
            shortDescription = "با افزایش زمان دم، هشیاری مغز و سطح انرژی طبیعی بدن را بالا ببرید.",
            durationSeconds = 120,
            iconEmoji = "☀️",
            isFree = false,
            inhaleSeconds = 4,
            holdSeconds = 2,
            exhaleSeconds = 2
        ),
        ContentItemEntity(
            id = 11,
            title = "تنفس همسان و متوازن (۵-۵-۵)",
            category = "BREATHING",
            shortDescription = "ریتم‌های کاملاً برابر دم، حبس و بازدم برای تعادل همه‌جانبه سیستم عصبی.",
            durationSeconds = 150,
            iconEmoji = "⚖️",
            isFree = true,
            inhaleSeconds = 5,
            holdSeconds = 5,
            exhaleSeconds = 5
        ),
        ContentItemEntity(
            id = 12,
            title = "تنفس عمیق ضد اضطراب (۴-۴-۸)",
            category = "BREATHING",
            shortDescription = "بازدم بسیار طولانی جهت تحریک عصب واگ و تخلیه کامل بار استرس.",
            durationSeconds = 180,
            iconEmoji = "🛡️",
            isFree = false,
            inhaleSeconds = 4,
            holdSeconds = 4,
            exhaleSeconds = 8
        ),

        // 3. Relaxing Ambient Sounds (5 items)
        ContentItemEntity(
            id = 13,
            title = "صدای ریزش باران آرامش‌بخش",
            category = "SOUND",
            shortDescription = "موسیقی بی‌انتهای باران پاییزی روی شیروانی و برگ درختان.",
            durationSeconds = 300,
            iconEmoji = "🌧️",
            isFree = true,
            soundType = "rain"
        ),
        ContentItemEntity(
            id = 14,
            title = "طنین آرامش امواج اقیانوس",
            category = "SOUND",
            shortDescription = "جزر و مد ملایم آب‌های زلال اقیانوس بر کرانه سواحل صخره‌ای.",
            durationSeconds = 300,
            iconEmoji = "🌊",
            isFree = true,
            soundType = "ocean"
        ),
        ContentItemEntity(
            id = 15,
            title = "نویز سفید شفابخش ذهن",
            category = "SOUND",
            shortDescription = "فرکانس پایدار و همگن نویز سفید برای حذف کامل تمرکززدای محیطی.",
            durationSeconds = 300,
            iconEmoji = "🤍",
            isFree = false,
            soundType = "white"
        ),
        ContentItemEntity(
            id = 16,
            title = "زمزمه نسیم پاییزی در جنگل",
            category = "SOUND",
            shortDescription = "عبور ملایم باد از میان شاخسار درختان کهنسال صنوبر و بید.",
            durationSeconds = 300,
            iconEmoji = "🌲",
            isFree = false,
            soundType = "forest"
        ),
        ContentItemEntity(
            id = 17,
            title = "صدای دنج ترک‌خوردن هیزم آتش",
            category = "SOUND",
            shortDescription = "گرمای نوستالژیک سوختن چوب در کلبه کوهستانی پاییزی.",
            durationSeconds = 300,
            iconEmoji = "🔥",
            isFree = false,
            soundType = "fire"
        ),
        ContentItemEntity(
            id = 18,
            title = "فرکانس عشق و هارمونی ذن (۵۲۸ هرتز)",
            category = "SOUND",
            shortDescription = "ارتعاشات التیام‌بخش فرکانس‌های ذن و سولفژیو برای مدیتیشن و توازن ذهن.",
            durationSeconds = 300,
            iconEmoji = "🧘",
            isFree = true,
            soundType = "zen"
        ),
        ContentItemEntity(
            id = 19,
            title = "طنین ناقوس‌های باد تبتی",
            category = "SOUND",
            shortDescription = "صدای روح‌نواز ناقوس‌ها و کاسه‌های تبتی در بستری از امواج آرام‌بخش.",
            durationSeconds = 300,
            iconEmoji = "🔔",
            isFree = false,
            soundType = "chimes"
        )
    )
}
