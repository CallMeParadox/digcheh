package com.digcheh.app;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataManager {
    private static final String PREF_NAME = "digcheh_prefs";
    private static final String KEY_LOGGED_FOODS = "logged_foods";
    private static final String KEY_CHEAT_DAYS = "cheat_days";
    private static final String KEY_BURNED_CALS = "burned_cals";
    private static final String KEY_USER_PROFILE = "user_profile";
    private static final String KEY_IS_ONBOARDED = "is_onboarded";

    private static DataManager instance;
    private final SharedPreferences prefs;
    private final Gson gson;
    private final List<FoodItem> foodCatalog = new ArrayList<>();
    private final List<LoggedFood> loggedFoods = new ArrayList<>();
    private UserProfile userProfile;
    private int cheatCycleDays = 3;
    private int burnedCalories = 0;

    private DataManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadFoodCatalog(context);
        loadSavedData();
    }

    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
        }
        return instance;
    }

    private void loadFoodCatalog(Context context) {
        foodCatalog.clear();

        // 1. خورش‌ها (جداگانه و تفکیک‌شده بدون برنج)
        addFoodToCatalog("stew_1", "خورش قورمه‌سبزی (بدون برنج)", "خورش‌ها", 290, 18, 12, 20, "کاسه متوسط", Arrays.asList("کاسه متوسط", "ملاقه", "قاشق غذاخوری"));
        addFoodToCatalog("stew_2", "خورش قیمه با سیب‌زمینی سرخ‌کرده", "خورش‌ها", 350, 16, 28, 22, "کاسه متوسط", Arrays.asList("کاسه متوسط", "ملاقه", "قاشق غذاخوری"));
        addFoodToCatalog("stew_3", "خورش قیمه خالی (بدون سیب‌زمینی)", "خورش‌ها", 250, 15, 14, 15, "کاسه متوسط", Arrays.asList("کاسه متوسط", "ملاقه", "قاشق غذاخوری"));
        addFoodToCatalog("stew_4", "سیب‌زمینی سرخ‌کرده خورش قیمه (خلال نازک)", "خورش‌ها", 95, 1, 14, 5, "مشت (۳۰ گرم)", Arrays.asList("مشت (۳۰ گرم)", "قاشق غذاخوری", "۱۰۰ گرم"));
        addFoodToCatalog("stew_5", "خورش قیمه بادمجان", "خورش‌ها", 370, 16, 20, 26, "کاسه متوسط", Arrays.asList("کاسه متوسط", "ملاقه", "قاشق غذاخوری"));
        addFoodToCatalog("stew_6", "خورش فسنجان با مرغ (ترش و شیرین)", "خورش‌ها", 500, 26, 22, 38, "کاسه متوسط", Arrays.asList("کاسه متوسط", "ملاقه", "قاشق غذاخوری"));
        addFoodToCatalog("stew_7", "خورش فسنجان با گوشت قلقلی", "خورش‌ها", 560, 24, 20, 44, "کاسه متوسط", Arrays.asList("کاسه متوسط", "ملاقه", "قاشق غذاخوری"));
        addFoodToCatalog("stew_8", "خورش بامیه با گوشت گوسفندی", "خورش‌ها", 240, 16, 12, 14, "کاسه متوسط", Arrays.asList("کاسه متوسط", "ملاقه", "قاشق غذاخوری"));
        addFoodToCatalog("stew_9", "خورش کرفس با گوشت", "خورش‌ها", 250, 17, 10, 16, "کاسه متوسط", Arrays.asList("کاسه متوسط", "ملاقه", "قاشق غذاخوری"));
        addFoodToCatalog("stew_10", "خورش آلو اسفناج با مرغ", "خورش‌ها", 310, 22, 28, 14, "کاسه متوسط", Arrays.asList("کاسه متوسط", "ملاقه", "قاشق غذاخوری"));
        addFoodToCatalog("stew_11", "مرغ زعفرانی مجلسی (یک ران کامل)", "خورش‌ها", 320, 28, 4, 22, "یک ران کامل", Arrays.asList("یک ران کامل", "سینه کامل", "تکه متوسط"));
        addFoodToCatalog("stew_12", "خورش مسما بادمجان با گوجه", "خورش‌ها", 320, 14, 18, 22, "کاسه متوسط", Arrays.asList("کاسه متوسط", "ملاقه"));

        // 2. برنج، پلوها و ته‌چین (جداگانه)
        addFoodToCatalog("rice_1", "چلو سفید آبکش با روغن/کره", "برنج و پلو", 280, 5, 54, 6, "بشقاب (۸ قاشق)", Arrays.asList("بشقاب (۸ قاشق)", "کفگیر معمولی", "قاشق غذاخوری", "لیوان پخته"));
        addFoodToCatalog("rice_2", "برنج کته زعفرانی با روغن", "برنج و پلو", 320, 5, 56, 9, "بشقاب معمولی", Arrays.asList("بشقاب معمولی", "کفگیر", "قاشق غذاخوری"));
        addFoodToCatalog("rice_3", "برنج کته رژیمی (بدون روغن)", "برنج و پلو", 210, 5, 50, 1, "بشقاب معمولی", Arrays.asList("بشقاب معمولی", "کفگیر", "قاشق غذاخوری"));
        addFoodToCatalog("rice_4", "برنج قهوه‌ای پخته رژیمی", "برنج و پلو", 180, 4, 42, 1, "کفگیر", Arrays.asList("کفگیر", "قاشق غذاخوری", "بشقاب"));
        addFoodToCatalog("rice_5", "ته‌چین مرغ زعفرانی اصیل", "برنج و پلو", 460, 24, 52, 18, "برش متوسط", Arrays.asList("برش متوسط", "تکه کوچک", "پرس رستورانی"));
        addFoodToCatalog("rice_6", "ته‌چین گوشت و بادمجان", "برنج و پلو", 520, 22, 50, 26, "برش متوسط", Arrays.asList("برش متوسط", "پرس کامل"));
        addFoodToCatalog("rice_7", "ته‌دیگ برنجی زعفرانی برشته", "برنج و پلو", 160, 2, 22, 8, "کف دست", Arrays.asList("کف دست", "برش کوچک"));
        addFoodToCatalog("rice_8", "ته‌دیگ نان لواش روغنی", "برنج و پلو", 140, 2, 18, 7, "کف دست", Arrays.asList("کف دست", "تکه"));
        addFoodToCatalog("rice_9", "ته‌دیگ سیب‌زمینی ورقه‌ای زعفرانی", "برنج و پلو", 70, 1, 10, 3, "یک برش (حلقه)", Arrays.asList("یک برش (حلقه)", "دو برش"));
        addFoodToCatalog("rice_10", "زرشک‌پلو زعفرانی (برنج خالی با زرشک)", "برنج و پلو", 310, 5, 58, 7, "بشقاب استاندارد", Arrays.asList("بشقاب استاندارد", "کفگیر", "قاشق"));
        addFoodToCatalog("rice_11", "باقالی‌پلو با شوید (بدون گوشت)", "برنج و پلو", 340, 9, 58, 8, "بشقاب استاندارد", Arrays.asList("بشقاب استاندارد", "کفگیر"));
        addFoodToCatalog("rice_12", "باقالی‌پلو با ماهیچه مجلسی", "برنج و پلو", 820, 48, 64, 38, "پرس کامل رستورانی", Arrays.asList("پرس کامل رستورانی", "نیم پرس"));
        addFoodToCatalog("rice_13", "لوبیاپلو با گوشت چرخ‌کرده و دارچین", "برنج و پلو", 480, 18, 62, 18, "بشقاب استاندارد", Arrays.asList("بشقاب استاندارد", "کفگیر", "قاشق غذاخوری"));
        addFoodToCatalog("rice_14", "عدس‌پلو با کشمش و پیازداغ", "برنج و پلو", 440, 14, 68, 12, "بشقاب استاندارد", Arrays.asList("بشقاب استاندارد", "کفگیر", "قاشق"));
        addFoodToCatalog("rice_15", "استامبولی پلو با سیب‌زمینی و گوجه", "برنج و پلو", 420, 8, 65, 14, "بشقاب استاندارد", Arrays.asList("بشقاب استاندارد", "کفگیر"));
        addFoodToCatalog("rice_16", "کلم‌پلو شیرازی با کوفته قلقلی", "برنج و پلو", 490, 20, 60, 19, "بشقاب استاندارد", Arrays.asList("بشقاب استاندارد", "کفگیر"));
        addFoodToCatalog("rice_17", "آلبالوپلو با مرغ", "برنج و پلو", 510, 22, 70, 16, "بشقاب استاندارد", Arrays.asList("بشقاب استاندارد", "کفگیر"));
        addFoodToCatalog("rice_18", "سبزی‌پلو با ماهی قزل‌آلا", "برنج و پلو", 610, 38, 56, 24, "پرس کامل", Arrays.asList("پرس کامل", "ماهی بدون پلو"));

        // 3. کباب‌ها و گریل (بدون برنج)
        addFoodToCatalog("kebab_1", "کباب کوبیده سنتی (بدون برنج)", "کباب‌ها", 270, 18, 4, 21, "یک سیخ (۱۰۰ گرم)", Arrays.asList("یک سیخ (۱۰۰ گرم)", "دو سیخ"));
        addFoodToCatalog("kebab_2", "جوجه‌کباب فیله / سینه زعفرانی", "کباب‌ها", 220, 34, 2, 8, "یک سیخ (۱۵۰ گرم)", Arrays.asList("یک سیخ (۱۵۰ گرم)", "تکه"));
        addFoodToCatalog("kebab_3", "جوجه‌کباب با استخوان / ران", "کباب‌ها", 310, 28, 2, 19, "یک سیخ", Arrays.asList("یک سیخ", "تکه"));
        addFoodToCatalog("kebab_4", "کباب برگ راسته گوسفندی", "کباب‌ها", 240, 36, 1, 10, "یک سیخ", Arrays.asList("یک سیخ"));
        addFoodToCatalog("kebab_5", "کباب چنجه گوسفندی لذیذ", "کباب‌ها", 290, 32, 1, 18, "یک سیخ", Arrays.asList("یک سیخ"));
        addFoodToCatalog("kebab_6", "کباب شیشلیک شاندیز", "کباب‌ها", 480, 38, 1, 35, "یک سیخ (۵ تکه)", Arrays.asList("یک سیخ (۵ تکه)", "تکه"));
        addFoodToCatalog("kebab_7", "کباب تابه‌ای خانگی با گوشت", "کباب‌ها", 170, 16, 3, 11, "یک عدد متوسط", Arrays.asList("یک عدد متوسط", "ساندویچ با نان"));
        addFoodToCatalog("kebab_8", "فیله مرغ گریل رژیمی", "کباب‌ها", 165, 31, 0, 4, "۱۰۰ گرم", Arrays.asList("۱۰۰ گرم", "یک تکه فیله"));
        addFoodToCatalog("kebab_9", "سینه مرغ آب‌پز رژیمی", "کباب‌ها", 150, 31, 0, 3, "۱۰۰ گرم", Arrays.asList("۱۰۰ گرم", "یک تکه کامل"));
        addFoodToCatalog("kebab_10", "تن ماهی در روغن", "کباب‌ها", 380, 32, 0, 28, "قوطی ۱۸۰ گرمی", Arrays.asList("قوطی ۱۸۰ گرمی", "قاشق غذاخوری"));
        addFoodToCatalog("kebab_11", "تن ماهی رژیمی در آب‌نمک", "کباب‌ها", 220, 42, 0, 4, "قوطی ۱۸۰ گرمی", Arrays.asList("قوطی ۱۸۰ گرمی", "قاشق غذاخوری"));

        // 4. نوشیدنی‌ها و کافه
        addFoodToCatalog("drink_1", "موهیتو نعنا و لیمو تازه", "نوشیدنی و کافه", 120, 1, 28, 0, "لیوان ۳۰۰ میلی‌لیتر", Arrays.asList("لیوان ۳۰۰ میلی‌لیتر", "ماگ بزرگ", "شیشه"));
        addFoodToCatalog("drink_2", "قهوه اسپرسو خالص", "نوشیدنی و کافه", 5, 0, 1, 0, "شات سینگل (۳۰ میل)", Arrays.asList("شات سینگل (۳۰ میل)", "شات دبل"));
        addFoodToCatalog("drink_3", "قهوه ترک با شکر کم", "نوشیدنی و کافه", 25, 0, 6, 0, "فنجان", Arrays.asList("فنجان", "فنجان تلخ بدون قند"));
        addFoodToCatalog("drink_4", "قهوه فرانسه / آمریکانو تلخ", "نوشیدنی و کافه", 5, 0, 1, 0, "ماگ ۲۵۰ میلی‌لیتر", Arrays.asList("ماگ ۲۵۰ میلی‌لیتر", "فنجان"));
        addFoodToCatalog("drink_5", "کاپوچینو با فوم شیر", "نوشیدنی و کافه", 90, 4, 9, 4, "فنجان ۲۰۰ میلی‌لیتر", Arrays.asList("فنجان ۲۰۰ میلی‌لیتر", "ماگ با شکر"));
        addFoodToCatalog("drink_6", "کافه لاته با شیر گرم", "نوشیدنی و کافه", 130, 6, 12, 6, "ماگ ۳۰۰ میلی‌لیتر", Arrays.asList("ماگ ۳۰۰ میلی‌لیتر", "شیشه"));
        addFoodToCatalog("drink_7", "هات چاکلت (شکلات داغ)", "نوشیدنی و کافه", 220, 6, 32, 8, "ماگ ۲۵۰ میلی‌لیتر", Arrays.asList("ماگ ۲۵۰ میلی‌لیتر"));
        addFoodToCatalog("drink_8", "چای سیاه دم‌کشیده اصیل ایرانی", "نوشیدنی و کافه", 2, 0, 0, 0, "استکان / فنجان", Arrays.asList("استکان / فنجان", "ماگ بزرگ"));
        addFoodToCatalog("drink_9", "چای سبز دم‌کرده", "نوشیدنی و کافه", 2, 0, 0, 0, "فنجان", Arrays.asList("فنجان", "ماگ"));
        addFoodToCatalog("drink_10", "دمنوش بابونه / گل‌گاوزبان", "نوشیدنی و کافه", 2, 0, 0, 0, "فنجان", Arrays.asList("فنجان"));
        addFoodToCatalog("drink_11", "دوغ محلی نعنایی سنتی", "نوشیدنی و کافه", 85, 4, 5, 4, "لیوان ۲۵۰ میلی‌لیتر", Arrays.asList("لیوان ۲۵۰ میلی‌لیتر", "بطری کوچک"));
        addFoodToCatalog("drink_12", "دوغ گازدار", "نوشیدنی و کافه", 70, 3, 5, 3, "لیوان ۲۵۰ میلی‌لیتر", Arrays.asList("لیوان ۲۵۰ میلی‌لیتر"));
        addFoodToCatalog("drink_13", "شربت خاکشیر و زعفران با گلاب", "نوشیدنی و کافه", 110, 0, 27, 0, "لیوان ۲۵۰ میلی‌لیتر", Arrays.asList("لیوان ۲۵۰ میلی‌لیتر"));
        addFoodToCatalog("drink_14", "شربت سکنجبین و خیار رنده‌شده", "نوشیدنی و کافه", 120, 1, 30, 0, "لیوان", Arrays.asList("لیوان"));
        addFoodToCatalog("drink_15", "آبمیوه طبیعی پرتقال", "نوشیدنی و کافه", 110, 2, 25, 0, "لیوان ۲۵۰ میلی‌لیتر", Arrays.asList("لیوان ۲۵۰ میلی‌لیتر"));
        addFoodToCatalog("drink_16", "آب هویج طبیعی تازه", "نوشیدنی و کافه", 95, 2, 22, 0, "لیوان", Arrays.asList("لیوان", "با بستنی"));
        addFoodToCatalog("drink_17", "آب طالبی تگری سنتی", "نوشیدنی و کافه", 140, 2, 34, 0, "لیوان ۳۰۰ میلی‌لیتر", Arrays.asList("لیوان ۳۰۰ میلی‌لیتر"));
        addFoodToCatalog("drink_18", "شیر پاستوریزه کم‌چرب (۱.۵٪)", "نوشیدنی و کافه", 105, 8, 12, 3, "لیوان ۲۴۰ میلی‌لیتر", Arrays.asList("لیوان ۲۴۰ میلی‌لیتر"));
        addFoodToCatalog("drink_19", "شیر پرچرب (۳٪)", "نوشیدنی و کافه", 150, 8, 12, 8, "لیوان ۲۴۰ میلی‌لیتر", Arrays.asList("لیوان ۲۴۰ میلی‌لیتر"));
        addFoodToCatalog("drink_20", "نوشابه کوکاکولا / پپسی معمولی", "نوشیدنی و کافه", 140, 0, 35, 0, "قوطی ۳۳۰ میلی‌لیتر", Arrays.asList("قوطی ۳۳۰ میلی‌لیتر", "لیوان"));
        addFoodToCatalog("drink_21", "نوشابه کوکا زیرو / پپسی مکس (بدون قند)", "نوشیدنی و کافه", 0, 0, 0, 0, "قوطی / لیوان", Arrays.asList("قوطی / لیوان"));

        // 5. صبحانه، نان و لبنیات
        addFoodToCatalog("bf_1", "نان سنگک برشته", "صبحانه و لبنیات", 75, 3, 15, 1, "کف دست", Arrays.asList("کف دست", "تکه بزرگ", "یک نان کامل"));
        addFoodToCatalog("bf_2", "نان بربری سنتی کنجدی", "صبحانه و لبنیات", 85, 3, 17, 1, "کف دست", Arrays.asList("کف دست", "تکه بزرگ", "یک نان کامل"));
        addFoodToCatalog("bf_3", "نان تافتون تنوری", "صبحانه و لبنیات", 50, 2, 10, 0, "کف دست", Arrays.asList("کف دست", "یک نان کامل"));
        addFoodToCatalog("bf_4", "نان لواش ماشینی", "صبحانه و لبنیات", 35, 1, 7, 0, "کف دست", Arrays.asList("کف دست", "یک نان کامل"));
        addFoodToCatalog("bf_5", "نان تست جو سبوس‌دار رژیمی", "صبحانه و لبنیات", 70, 3, 13, 1, "یک برش", Arrays.asList("یک برش", "دو برش"));
        addFoodToCatalog("bf_6", "پنیر تبریزی / لیقوان گوسفندی", "صبحانه و لبنیات", 75, 5, 1, 6, "قوطی کبریت (۳۰ گرم)", Arrays.asList("قوطی کبریت (۳۰ گرم)", "قاشق"));
        addFoodToCatalog("bf_7", "پنیر فتا کم‌نمک و کم‌چرب", "صبحانه و لبنیات", 50, 4, 1, 3, "قوطی کبریت (۳۰ گرم)", Arrays.asList("قوطی کبریت (۳۰ گرم)"));
        addFoodToCatalog("bf_8", "پنیر خامه‌ای صبحانه", "صبحانه و لبنیات", 95, 2, 1, 9, "قوطی کبریت", Arrays.asList("قوطی کبریت", "قاشق غذاخوری"));
        addFoodToCatalog("bf_9", "تخم‌مرغ آب‌پز سفت", "صبحانه و لبنیات", 72, 6, 0, 5, "یک عدد", Arrays.asList("یک عدد", "دو عدد"));
        addFoodToCatalog("bf_10", "تخم‌مرغ نیمرو با کره/روغن", "صبحانه و لبنیات", 115, 6, 1, 10, "یک عدد", Arrays.asList("یک عدد", "دو عدد"));
        addFoodToCatalog("bf_11", "املت گوجه‌فرنگی سنتی قهوه‌خانه‌ای", "صبحانه و لبنیات", 220, 11, 12, 15, "یک پرس متوسط", Arrays.asList("یک پرس متوسط", "بشقاب بزرگ"));
        addFoodToCatalog("bf_12", "سوسیس تخم‌مرغ", "صبحانه و لبنیات", 360, 16, 8, 28, "یک پرس", Arrays.asList("یک پرس"));
        addFoodToCatalog("bf_13", "کره پاستوریزه حیوانی", "صبحانه و لبنیات", 75, 0, 0, 8, "قالب کوچک (۱۰ گرم)", Arrays.asList("قالب کوچک (۱۰ گرم)", "قاشق غذاخوری"));
        addFoodToCatalog("bf_14", "خامه صبحانه پاستوریزه", "صبحانه و لبنیات", 55, 1, 1, 6, "یک قاشق غذاخوری", Arrays.asList("یک قاشق غذاخوری"));
        addFoodToCatalog("bf_15", "عسل طبیعی کوهستان", "صبحانه و لبنیات", 60, 0, 17, 0, "یک قاشق غذاخوری", Arrays.asList("یک قاشق غذاخوری", "قاشق مرباخوری"));
        addFoodToCatalog("bf_16", "مربای آلبالو / هویج", "صبحانه و لبنیات", 50, 0, 13, 0, "یک قاشق غذاخوری", Arrays.asList("یک قاشق غذاخوری"));
        addFoodToCatalog("bf_17", "حلوا ارده شکری اردکان", "صبحانه و لبنیات", 150, 4, 16, 8, "۳۰ گرم (۲ قاشق)", Arrays.asList("۳۰ گرم (۲ قاشق)", "یک قاشق غذاخوری"));
        addFoodToCatalog("bf_18", "ارده کنجد خالص با شیره انگور", "صبحانه و لبنیات", 140, 3, 16, 7, "یک قاشق غذاخوری مخلوط", Arrays.asList("یک قاشق غذاخوری مخلوط"));
        addFoodToCatalog("bf_19", "گردو ایرانی مغز سالم", "صبحانه و لبنیات", 32, 1, 1, 3, "یک عدد مغز گردو کامل", Arrays.asList("یک عدد مغز گردو کامل", "مشت (۳۰ گرم)"));
        addFoodToCatalog("bf_20", "زیتون پرورده رودبار با گردو و انار", "صبحانه و لبنیات", 60, 1, 4, 5, "یک قاشق غذاخوری", Arrays.asList("یک قاشق غذاخوری", "پیاله کوچک"));

        // 6. سنتی، آش و خوراک
        addFoodToCatalog("trad_1", "آش رشته با کشک و پیازداغ و نعناداغ", "سنتی و آش", 310, 12, 46, 9, "کاسه متوسط", Arrays.asList("کاسه متوسط", "ملاقه", "کاسه بزرگ"));
        addFoodToCatalog("trad_2", "آش جو سنتی با کشک", "سنتی و آش", 280, 11, 44, 7, "کاسه متوسط", Arrays.asList("کاسه متوسط", "ملاقه"));
        addFoodToCatalog("trad_3", "آش شله‌قلمکار تهرانی با گوشت", "سنتی و آش", 340, 18, 48, 10, "کاسه متوسط", Arrays.asList("کاسه متوسط", "ملاقه"));
        addFoodToCatalog("trad_4", "آش دوغ اردبیل و سرعین", "سنتی و آش", 200, 9, 30, 4, "کاسه متوسط", Arrays.asList("کاسه متوسط", "ملاقه"));
        addFoodToCatalog("trad_5", "سوپ جو پرک با شیر و قارچ", "سنتی و آش", 210, 8, 26, 8, "کاسه متوسط", Arrays.asList("کاسه متوسط", "پیش‌دستی"));
        addFoodToCatalog("trad_6", "سوپ مرغ و ورمیشل سبک", "سنتی و آش", 120, 8, 16, 3, "کاسه متوسط", Arrays.asList("کاسه متوسط", "ملاقه"));
        addFoodToCatalog("trad_7", "کشک بادمجان اصیل مجلسی", "سنتی و آش", 280, 8, 18, 20, "یک پیش‌دستی", Arrays.asList("یک پیش‌دستی", "قاشق غذاخوری"));
        addFoodToCatalog("trad_8", "میرزاقاسمی گیلانی با تخم‌مرغ", "سنتی و آش", 240, 8, 14, 17, "یک پیش‌دستی", Arrays.asList("یک پیش‌دستی", "قاشق غذاخوری"));
        addFoodToCatalog("trad_9", "آبگوشت دیزی سنتی (یک پرس کامل با گوشت کوبیده)", "سنتی و آش", 750, 42, 60, 36, "یک دیزی کامل", Arrays.asList("یک دیزی کامل", "کاسه آبگوشت تنها", "گوشت کوبیده تنها"));
        addFoodToCatalog("trad_10", "حلیم گندم با گوشت بوقلمون و دارچین", "سنتی و آش", 280, 16, 42, 6, "کاسه متوسط", Arrays.asList("کاسه متوسط", "کاسه با شکر و روغن", "ملاقه"));
        addFoodToCatalog("trad_11", "کوفته تبریزی مغزدار (با آلو و گردو)", "سنتی و آش", 390, 26, 28, 19, "یک عدد کوفته کامل", Arrays.asList("یک عدد کوفته کامل"));
        addFoodToCatalog("trad_12", "دلمه برگ مو با گوشت و لپه", "سنتی و آش", 45, 2, 6, 2, "یک عدد", Arrays.asList("یک عدد", "۴ عدد دلمه"));
        addFoodToCatalog("trad_13", "کتلت گوشت خانگی ترد", "سنتی و آش", 130, 8, 9, 7, "یک عدد", Arrays.asList("یک عدد", "ساندویچ با نان"));
        addFoodToCatalog("trad_14", "کوکو سبزی با زرشک و گردو", "سنتی و آش", 110, 5, 6, 8, "یک برش مثلثی", Arrays.asList("یک برش مثلثی", "ساندویچ با نان"));

        // 7. فست‌فود و ساندویچ
        addFoodToCatalog("fast_1", "سیب‌زمینی سرخ‌کرده فست‌فودی", "فست‌فود", 340, 4, 46, 16, "بسته متوسط", Arrays.asList("بسته متوسط", "بسته کوچک", "۱۰۰ گرم", "خلال"));
        addFoodToCatalog("fast_2", "ساندویچ فلافل با نان باگت و ترشی", "فست‌فود", 380, 14, 58, 12, "یک ساندویچ کامل", Arrays.asList("یک ساندویچ کامل", "یک قرص فلافل تکی"));
        addFoodToCatalog("fast_3", "ساندویچ سوسیس بندری تند", "فست‌فود", 490, 16, 52, 24, "یک ساندویچ باگت", Arrays.asList("یک ساندویچ باگت"));
        addFoodToCatalog("fast_4", "ساندویچ سالاد الویه با مرغ", "فست‌فود", 520, 20, 54, 25, "یک ساندویچ کامل", Arrays.asList("یک ساندویچ کامل", "قاشق غذاخوری الویه"));
        addFoodToCatalog("fast_5", "همبرگر گوشت دست‌ساز", "فست‌فود", 450, 24, 42, 21, "یک عدد ساندویچ", Arrays.asList("یک عدد ساندویچ", "با پنیر چیزبرگر"));
        addFoodToCatalog("fast_6", "پیتزا مخلوط یا پپرونی", "فست‌فود", 240, 11, 24, 12, "یک اسلایس (برش)", Arrays.asList("یک اسلایس (برش)", "مینی‌پیتزا کامل"));
        addFoodToCatalog("fast_7", "فیله استریپس سوخاری مرغ", "فست‌فود", 160, 14, 10, 7, "یک تکه فیله", Arrays.asList("یک تکه فیله", "۳ تکه با سیب‌زمینی"));

        // 8. میوه، سالاد و میان‌وعده
        addFoodToCatalog("snack_1", "خرما مضافتی بم", "میوه و میان‌وعده", 23, 0, 6, 0, "یک عدد", Arrays.asList("یک عدد", "۳ عدد"));
        addFoodToCatalog("snack_2", "سیب درختی متوسط", "میوه و میان‌وعده", 75, 0, 19, 0, "یک عدد متوسط", Arrays.asList("یک عدد متوسط", "یک عدد بزرگ"));
        addFoodToCatalog("snack_3", "موز شیرین", "میوه و میان‌وعده", 105, 1, 27, 0, "یک عدد متوسط", Arrays.asList("یک عدد متوسط", "یک عدد کوچک"));
        addFoodToCatalog("snack_4", "پرتقال تامسون", "میوه و میان‌وعده", 65, 1, 16, 0, "یک عدد", Arrays.asList("یک عدد"));
        addFoodToCatalog("snack_5", "هندوانه شیرین تابستانی", "میوه و میان‌وعده", 30, 1, 8, 0, "یک قاچ (۱۰۰ گرم)", Arrays.asList("یک قاچ (۱۰۰ گرم)", "کاسه مکعبی"));
        addFoodToCatalog("snack_6", "طالبی / خربزه مشهدی", "میوه و میان‌وعده", 35, 1, 9, 0, "یک قاچ ۱۰۰ گرم", Arrays.asList("یک قاچ ۱۰۰ گرم"));
        addFoodToCatalog("snack_7", "انار دانه شده سرخ", "میوه و میان‌وعده", 100, 2, 23, 1, "یک پیاله (۱۰۰ گرم)", Arrays.asList("یک پیاله (۱۰۰ گرم)", "یک انار کامل"));
        addFoodToCatalog("snack_8", "سالاد شیرازی با آبغوره و نعنا", "میوه و میان‌وعده", 40, 1, 8, 0, "کاسه سالادخوری", Arrays.asList("کاسه سالادخوری", "پیش‌دستی"));
        addFoodToCatalog("snack_9", "ماست کم‌چرب پاستوریزه (۱.۵٪)", "میوه و میان‌وعده", 70, 5, 7, 2, "پیاله ماست‌خوری", Arrays.asList("پیاله ماست‌خوری", "لیوان"));
        addFoodToCatalog("snack_10", "ماست و خیار سنتی با کشمش و نعنا", "میوه و میان‌وعده", 95, 5, 11, 3, "پیاله ماست‌خوری", Arrays.asList("پیاله ماست‌خوری"));
        addFoodToCatalog("snack_11", "ماست موسیر چکیده لذیذ", "میوه و میان‌وعده", 110, 6, 8, 6, "پیاله", Arrays.asList("پیاله", "قاشق غذاخوری"));
        addFoodToCatalog("snack_12", "پسته شور یا خام رفسنجان", "میوه و میان‌وعده", 170, 6, 8, 14, "مشت (۳۰ گرم)", Arrays.asList("مشت (۳۰ گرم)", "۱۰ عدد دانه"));
        addFoodToCatalog("snack_13", "بادام درختی بو داده", "میوه و میان‌وعده", 175, 6, 6, 15, "مشت (۳۰ گرم)", Arrays.asList("مشت (۳۰ گرم)", "۱۰ عدد دانه"));
        addFoodToCatalog("snack_14", "تخمه آفتابگردان بو داده", "میوه و میان‌وعده", 140, 5, 6, 12, "مشت (با پوست)", Arrays.asList("مشت (با پوست)", "۵۰ گرم"));
        addFoodToCatalog("snack_15", "شکلات تلخ ۷۰٪", "میوه و میان‌وعده", 55, 1, 4, 4, "یک مربع (۱۰ گرم)", Arrays.asList("یک مربع (۱۰ گرم)", "دو مربع"));
    }

    private void addFoodToCatalog(String id, String name, String category, int cals, int prot, int carbs, int fat, String defaultUnit, List<String> units) {
        foodCatalog.add(new FoodItem(id, name, category, cals, prot, carbs, fat, defaultUnit, units));
    }

    private void loadSavedData() {
        cheatCycleDays = prefs.getInt(KEY_CHEAT_DAYS, 3);
        burnedCalories = prefs.getInt(KEY_BURNED_CALS, 0); // Start with 0 burned calories
        String profileJson = prefs.getString(KEY_USER_PROFILE, null);
        if (profileJson != null) {
            userProfile = gson.fromJson(profileJson, UserProfile.class);
        }
        if (userProfile == null) {
            userProfile = new UserProfile();
        }

        String json = prefs.getString(KEY_LOGGED_FOODS, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<LoggedFood>>() {}.getType();
            List<LoggedFood> saved = gson.fromJson(json, type);
            if (saved != null) {
                loggedFoods.addAll(saved);
            }
        }
        // ZERO pre-filled demo foods! Clean slate for user!
    }

    public List<FoodItem> getFoodCatalog() {
        return foodCatalog;
    }

    public List<LoggedFood> getLoggedFoodsForMeal(String mealType) {
        List<LoggedFood> result = new ArrayList<>();
        for (LoggedFood f : loggedFoods) {
            if (f.getMealType().equalsIgnoreCase(mealType)) {
                result.add(f);
            }
        }
        return result;
    }

    public void addLoggedFood(LoggedFood food) {
        loggedFoods.add(food);
        saveFoods();
    }

    public void removeLoggedFood(String id) {
        for (int i = 0; i < loggedFoods.size(); i++) {
            if (loggedFoods.get(i).getId().equals(id)) {
                loggedFoods.remove(i);
                break;
            }
        }
        saveFoods();
    }

    private void saveFoods() {
        String json = gson.toJson(loggedFoods);
        prefs.edit().putString(KEY_LOGGED_FOODS, json).apply();
    }

    public int getTotalConsumedCalories() {
        int sum = 0;
        for (LoggedFood f : loggedFoods) {
            sum += f.getCalories();
        }
        return sum;
    }

    public int getTotalProtein() {
        int sum = 0;
        for (LoggedFood f : loggedFoods) {
            sum += f.getProtein();
        }
        return sum;
    }

    public int getTotalCarbs() {
        int sum = 0;
        for (LoggedFood f : loggedFoods) {
            sum += f.getCarbs();
        }
        return sum;
    }

    public int getTotalFat() {
        int sum = 0;
        for (LoggedFood f : loggedFoods) {
            sum += f.getFat();
        }
        return sum;
    }

    public int getMealCalories(String mealType) {
        int sum = 0;
        for (LoggedFood f : loggedFoods) {
            if (f.getMealType().equalsIgnoreCase(mealType)) {
                sum += f.getCalories();
            }
        }
        return sum;
    }

    public int getCheatCycleDays() {
        return cheatCycleDays;
    }

    public void setCheatCycleDays(int days) {
        this.cheatCycleDays = days;
        prefs.edit().putInt(KEY_CHEAT_DAYS, days).apply();
    }

    public int getBurnedCalories() {
        return burnedCalories;
    }

    public void addBurnedCalories(int cals) {
        this.burnedCalories += cals;
        prefs.edit().putInt(KEY_BURNED_CALS, burnedCalories).apply();
    }

    public boolean isUserOnboarded() {
        return prefs.getBoolean(KEY_IS_ONBOARDED, false);
    }

    public void setUserOnboarded(boolean onboarded) {
        prefs.edit().putBoolean(KEY_IS_ONBOARDED, onboarded).apply();
    }

    public UserProfile getUserProfile() {
        if (userProfile == null) {
            userProfile = new UserProfile();
        }
        return userProfile;
    }

    public void saveUserProfile(UserProfile profile) {
        this.userProfile = profile;
        this.userProfile.calculateMetabolism();
        String json = gson.toJson(this.userProfile);
        prefs.edit().putString(KEY_USER_PROFILE, json).putBoolean(KEY_IS_ONBOARDED, true).apply();
    }
}
