package com.sm.model;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public enum TZone {
    UTC("0", "Dateline Standard Time", "(GMT-12:00) International Date Line West", TimeZone.getTimeZone("UTC")),
    SST("1", "Samoa Standard Time", "(GMT-11:00) Midway Island, Samoa", TimeZone.getTimeZone("Pacific/Apia")),
    HST("2", "Hawaiian Standard Time", "(GMT-10:00) Hawaii", TimeZone.getTimeZone("Pacific/Honolulu")),
    AST("3", "Alaskan Standard Time", "(GMT-09:00) Alaska", TimeZone.getTimeZone("America/Anchorage")),
    PST("4", "Pacific Standard Time", "(GMT-08:00) Pacific Time (US and Canada); Tijuana", TimeZone.getTimeZone("PST")),
    MNST("10", "Mountain Standard Time", "(GMT-07:00) Mountain Time (US and Canada)", TimeZone.getTimeZone("America/Denver")),
    MST2("13", "Mexico Standard Time 2", "(GMT-07:00) Chihuahua, La Paz, Mazatlan", TimeZone.getTimeZone("America/Chihuahua")),
    USMST("15", "U.S. Mountain Standard Time", "(GMT-07:00) Arizona", TimeZone.getTimeZone("America/Phoenix")),
    CST("20", "Central Standard Time", "(GMT-06:00) Central Time (US and Canada", TimeZone.getTimeZone("CST")),
    CCST("25", "Canada Central Standard Time", "(GMT-06:00) Saskatchewan", TimeZone.getTimeZone("America/Regina")),
    MST("30", "Mexico Standard Time", "(GMT-06:00) Guadalajara, Mexico City, Monterrey", TimeZone.getTimeZone("America/Mexico_City")),
    CAST("33", "Central America Standard Time", "(GMT-06:00) Central America", TimeZone.getTimeZone("America/Regina")),
    EST("35", "Eastern Standard Time", "(GMT-05:00) Eastern Time (US and Canada)", TimeZone.getTimeZone("America/New_York")),
    USEST("40", "U.S. Eastern Standard Time", "(GMT-05:00) Indiana (East)", TimeZone.getTimeZone("America/Indianapolis")),
    SAPST("45", "S.A. Pacific Standard Time", "(GMT-05:00) Bogota, Lima, Quito", TimeZone.getTimeZone("America/Bogota")),
    ATST("50", "Atlantic Standard Time", "(GMT-04:00) Atlantic Time (Canada)", TimeZone.getTimeZone("America/Halifax")),
    SAWST("55", "S.A. Western Standard Time", "(GMT-04:00) Caracas, La Paz", TimeZone.getTimeZone("America/Caracas")),
    PSAST("56", "Pacific S.A. Standard Time", "(GMT-04:00) Santiago", TimeZone.getTimeZone("America/Caracas")),
    NLST("60", "Newfoundland and Labrador Standard Time", "(GMT-03:30) Newfoundland and Labrador", TimeZone.getTimeZone("America/St_Johns")),
    ESAST("65", "E. South America Standard Time", "(GMT-03:00) Brasilia", TimeZone.getTimeZone("America/Sao_Paulo")),
    SAEST("70", "S.A. Eastern Standard Time", "(GMT-03:00) Buenos Aires, Georgetown", TimeZone.getTimeZone("America/Buenos_Aires")),
    GST("73", "Greenland Standard Time", "(GMT-03:00) Greenland", TimeZone.getTimeZone("America/Godthab")),
    MAST("75", "Mid-Atlantic Standard Time", "(GMT-02:00) Mid-Atlantic", TimeZone.getTimeZone("Etc/GMT+2")),
    AAST("80", "Azores Standard Time", "(GMT-01:00) Azores", TimeZone.getTimeZone("Atlantic/Azores")),
    CVST("83", "Cape Verde Standard Time", "(GMT-01:00) Cape Verde Islands", TimeZone.getTimeZone("Atlantic/Cape_Verde")),
    GMT("85", "GMT Standard Time", "(GMT) Greenwich Mean Time: Dublin, Edinburgh, Lisbon, London", TimeZone.getTimeZone("Europe/London")),
    GSTCM("90", "Greenwich Standard Time", "(GMT) Casablanca, Monrovia", TimeZone.getTimeZone("Atlantic/Reykjavik")),
    CEST("95", "Central Europe Standard Time", "(GMT+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague", TimeZone.getTimeZone("Europe/Budapest")),
    CEST2("100", "Central European Standard Time", "(GMT+01:00) Sarajevo, Skopje, Warsaw, Zagreb", TimeZone.getTimeZone("Europe/Warsaw")),
    RST("105", "Romance Standard Time", "(GMT+01:00) Brussels, Copenhagen, Madrid, Paris", TimeZone.getTimeZone("Europe/Paris")),
    WEST("110", "W. Europe Standard Time", "(GMT+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna", TimeZone.getTimeZone("Europe/Berlin")),
    WECAST("113", "W. Central Africa Standard Time", "(GMT+01:00) West Central Africa", TimeZone.getTimeZone("Africa/Lagos")),
    EEST("115", "E. Europe Standard Time", "(GMT+02:00) Bucharest", TimeZone.getTimeZone("Europe/Minsk")),
    ESTC("120", "Egypt Standard Time", "(GMT+02:00) Cairo", TimeZone.getTimeZone("Africa/Cairo")),
    FLEST("125","FLE Standard Time","(GMT+02:00) Helsinki, Kiev, Riga, Sofia, Tallinn, Vilnius",TimeZone.getTimeZone("Europe/Kiev")),
    GTBST("130","GTB Standard Time","(GMT+02:00) Athens, Istanbul, Minsk",TimeZone.getTimeZone("Europe/Istanbul")),
    IST("135","Israel Standard Time","(GMT+02:00) Jerusalem",TimeZone.getTimeZone("Asia/Jerusalem")),
    SAST("140","South Africa Standard Time","(GMT+02:00) Harare, Pretoria",TimeZone.getTimeZone("Africa/Johannesburg")),
    RUST("145","Russian Standard Time","(GMT+03:00) Moscow, St. Petersburg, Volgograd",TimeZone.getTimeZone("Europe/Moscow")),
    ASTK("150","Arab Standard Time","(GMT+03:00) Kuwait, Riyadh",TimeZone.getTimeZone("Asia/Riyadh")),
    EAST("155","E. Africa Standard Time","(GMT+03:00) Nairobi",TimeZone.getTimeZone("Africa/Nairobi")),
    ASTB("158","Arabic Standard Time","(GMT+03:00) Baghdad",TimeZone.getTimeZone("Asia/Baghdad")),
    ISTT("160","Iran Standard Time","(GMT+03:30) Tehran",TimeZone.getTimeZone("Asia/Tehran")),
    ASTA("165","Arabian Standard Time","(GMT+04:00) Abu Dhabi, Muscat",TimeZone.getTimeZone("Asia/Dubai")),
    CSTB("170","Caucasus Standard Time","(GMT+04:00) Baku, Tbilisi, Yerevan",TimeZone.getTimeZone("Asia/Yerevan")),
    TISAST("175","Transitional Islamic State of Afghanistan Standard Time","(GMT+04:30) Kabul",TimeZone.getTimeZone("Asia/Kabul")),
    ESTE("180","Ekaterinburg Standard Time","(GMT+05:00) Ekaterinburg",TimeZone.getTimeZone("Asia/Yekaterinburg")),
    WAST("185","West Asia Standard Time","(GMT+05:00) Islamabad, Karachi, Tashkent",TimeZone.getTimeZone("Asia/Tashkent")),
    ISTC("190","India Standard Time","(GMT+05:30) Chennai, Kolkata, Mumbai, New Delhi",TimeZone.getTimeZone("Asia/Calcutta")),
    NST("193","Nepal Standard Time","(GMT+05:45) Kathmandu",TimeZone.getTimeZone("Asia/Katmandu")),
    CASTAD("195","Central Asia Standard Time","(GMT+06:00) Astana, Dhaka",TimeZone.getTimeZone("Asia/Almaty")),
    SLST("200","Sri Lanka Standard Time","(GMT+06:00) Sri Jayawardenepura",TimeZone.getTimeZone("Asia/Colombo")),
    NCAST("201","N. Central Asia Standard Time","(GMT+06:00) Almaty, Novosibirsk",TimeZone.getTimeZone("Asia/Novosibirsk")),
    MSTY("203","Myanmar Standard Time","(GMT+06:30) Yangon Rangoon",TimeZone.getTimeZone("Asia/Rangoon")),
    SEAST("205","S.E. Asia Standard Time","(GMT+07:00) Bangkok, Hanoi, Jakarta",TimeZone.getTimeZone("Asia/Bangkok")),
    NAST("207","North Asia Standard Time","(GMT+07:00) Krasnoyarsk",TimeZone.getTimeZone("Asia/Krasnoyarsk")),
    CSTBC("210","China Standard Time","(GMT+08:00) Beijing, Chongqing, Hong Kong SAR, Urumqi",TimeZone.getTimeZone("Asia/Shanghai")),
    SSTK("215","Singapore Standard Time","(GMT+08:00) Kuala Lumpur, Singapore",TimeZone.getTimeZone("Asia/Singapore")),
    TST("220","Taipei Standard Time","(GMT+08:00) Taipei",TimeZone.getTimeZone("Asia/Taipei")),
    WASTP("225","W. Australia Standard Time","(GMT+08:00) Perth",TimeZone.getTimeZone("Australia/Perth")),
    NAEST("227","North Asia East Standard Time","(GMT+08:00) Irkutsk, Ulaanbaatar",TimeZone.getTimeZone("Asia/Irkutsk")),
    KST("230","Korea Standard Time","(GMT+09:00) Seoul",TimeZone.getTimeZone("Asia/Seoul")),
    TSTO("235","Tokyo Standard Time","(GMT+09:00) Osaka, Sapporo, Tokyo",TimeZone.getTimeZone("Asia/Tokyo")),
    YST("240","Yakutsk Standard Time","(GMT+09:00) Yakutsk",TimeZone.getTimeZone("Asia/Yakutsk")),
    AUSCST("245","A.U.S. Central Standard Time","(GMT+09:30) Darwin",TimeZone.getTimeZone("Australia/Darwin")),
    CASTA("250","Cen. Australia Standard Time","(GMT+09:30) Adelaide",TimeZone.getTimeZone("Australia/Adelaide")),
    AUSEST("255","A.U.S. Eastern Standard Time","(GMT+10:00) Canberra, Melbourne, Sydney",TimeZone.getTimeZone("Australia/Sydney")),
    EASTB("260","E. Australia Standard Time","(GMT+10:00) Brisbane",TimeZone.getTimeZone("Australia/Brisbane")),
    TSTH("265","Tasmania Standard Time","(GMT+10:00) Hobart",TimeZone.getTimeZone("Australia/Hobart")),
    VST("270","Vladivostok Standard Time","(GMT+10:00) Vladivostok",TimeZone.getTimeZone("Asia/Vladivostok")),
    WPST("275","West Pacific Standard Time","(GMT+10:00) Guam, Port Moresby",TimeZone.getTimeZone("Pacific/Port_Moresby")),
    CPST("280","Central Pacific Standard Time","(GMT+11:00) Magadan, Solomon Islands, New Caledonia",TimeZone.getTimeZone("Pacific/Guadalcanal")),
    FIST("285","Fiji Islands Standard Time","(GMT+12:00) Fiji Islands, Kamchatka, Marshall Islands",TimeZone.getTimeZone("Pacific/Fiji")),
    NZST("290","New Zealand Standard Time","(GMT+12:00) Auckland, Wellington",TimeZone.getTimeZone("Pacific/Auckland")),
    TSTN("300","Tonga Standard Time","(GMT+13:00) Nuku'alofa",TimeZone.getTimeZone("Pacific/Tongatapu"));



    private String index;
    private String name;
    private String description;
    private TimeZone timeZone;

    private static Map<String, TimeZone> tzMap = new ConcurrentHashMap<>();

    TZone(String index, String name, String description, TimeZone timeZone) {
        this.index = index;
        this.name = name;
        this.description = description;
        this.timeZone = timeZone;

    }

    static {
        for (TZone tz : TZone.values()) {
            tzMap.put(tz.index, tz.timeZone);
        }
    }

    public static TimeZone getTimeZoneByIndex(String index) {
        if (index == null) {
            return null;
        }
        return tzMap.get(index);
    }

}
