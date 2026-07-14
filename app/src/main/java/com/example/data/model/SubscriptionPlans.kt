package com.example.data.model

object SubscriptionPrices {
    const val PRICE_FREE = 0L
    const val PRICE_MONTHLY = 149000L
    const val PRICE_YEARLY = 1070000L
    const val PRICE_PROFESSIONAL = 499000L
}

enum class SubscriptionPlans(
    val id: String,
    val persianName: String,
    val priceTomans: Long,
    val period: String,
    val features: List<String>,
    val badge: String? = null
) {
    FREE(
        id = "FREE",
        persianName = "رایگان",
        priceTomans = SubscriptionPrices.PRICE_FREE,
        period = "همیشگی",
        features = listOf(
            "۵ گفتگو در روز",
            "موتور هوش مصنوعی سریع و سبک آراما",
            "تمرینات پایه",
            "مدیتیشن‌های عمومی"
        )
    ),
    MONTHLY(
        id = "MONTHLY",
        persianName = "ماهانه",
        priceTomans = SubscriptionPrices.PRICE_MONTHLY,
        period = "ماهانه",
        features = listOf(
            "گفتگوهای نامحدود",
            "موتور هوشمند و روان آراما (نسخه استاندارد)",
            "تمام تمرینات و مدیتیشن‌ها",
            "گزارش‌های تحلیلی احساسات",
            "پشتیبانی اولویت‌دار"
        ),
        badge = "محبوب‌ترین"
    ),
    YEARLY(
        id = "YEARLY",
        persianName = "سالانه",
        priceTomans = SubscriptionPrices.PRICE_YEARLY,
        period = "سالانه",
        features = listOf(
            "تمام امکانات طرح ماهانه",
            "موتور فوق‌پیشرفته و تحلیلی آراما (تفکر عمیق)",
            "۴۰٪ تخفیف کل دوره",
            "دسترسی زودهنگام به ویژگی‌های جدید",
            "مشاوره روان‌شناختی ماهانه"
        ),
        badge = "۴۰٪ تخفیف"
    ),
    PROFESSIONAL(
        id = "PROFESSIONAL",
        persianName = "حرفه‌ای",
        priceTomans = SubscriptionPrices.PRICE_PROFESSIONAL,
        period = "ماهانه",
        features = listOf(
            "تمام امکانات طرح سالانه",
            "استفاده نامحدود از تمام موتورهای تخصصی آراما",
            "پنل مدیریت بیماران و روان‌درمانگران",
            "خروجی گزارش‌های پیشرفته با فرمت PDF/Excel",
            "پشتیبانی ۲۴ ساعته اختصاصی"
        )
    );

    companion object {
        fun fromId(id: String): SubscriptionPlans {
            return values().find { it.id.equals(id, ignoreCase = true) } ?: FREE
        }
    }
}
