package com.pocketdev.window_droplet.model

enum class Season(
    val displayName: String,
    val locationName: String,
    val description: String,
    val windowTintColor: Long,
    val dropletColor: Long,
    val dropletHighlightColor: Long,
    val rainDropDensity: Float,
    val lateralWindForce: Float = 0f,
    val slideFriction: Float = 0.96f
) {
    MONSOON_CITY(
        displayName = "শহুরে বৃষ্টি (City Monsoon)",
        locationName = "শহরের বহুতল জানালার কাচ",
        description = "ঘন বৃষ্টি ও ভেজা কাচ। পেছনে শহরের ট্রাফিকের নিয়ন আলো ও অজস্র পানির ফোঁটা।",
        windowTintColor = 0xFF0D1B2A,
        dropletColor = 0xB390CAF9,
        dropletHighlightColor = 0xF0FFFFFF,
        rainDropDensity = 1.0f,
        lateralWindForce = 0f,
        slideFriction = 0.96f
    ),
    VEHICLE_WINDSHIELD(
        displayName = "গাড়ির উইন্ডশিল্ড (Car Windshield)",
        locationName = "চলন্ত যানবাহনের সামনের কাচ",
        description = "গতিশীল হাইওয়ে। বাতাসের টানে পানির বিন্দুগুলো দ্রুত পিছলে যায়।",
        windowTintColor = 0xFF1B263B,
        dropletColor = 0xB380DEEA,
        dropletHighlightColor = 0xF5FFFFFF,
        rainDropDensity = 1.2f,
        lateralWindForce = 0.45f,
        slideFriction = 0.98f
    ),
    WINTER_FROST(
        displayName = "শীতের কুয়াশা (Winter Frost)",
        locationName = "কুয়াশাচ্ছন্ন বরফের কাচ",
        description = "শীতল কুয়াশা ও শিশিরবিন্দু। কাচ অতিরিক্ত মসৃণ ও পিচ্ছিল।",
        windowTintColor = 0xFF142433,
        dropletColor = 0xB3E0F7FA,
        dropletHighlightColor = 0xFFFFFFFF,
        rainDropDensity = 0.65f,
        lateralWindForce = -0.2f,
        slideFriction = 0.99f
    ),
    VILLAGE_RAIN(
        displayName = "গ্রামের স্নিগ্ধ বৃষ্টি (Village Rain)",
        locationName = "গ্রামের বাড়ির কাঠের ফ্রেমের কাচ",
        description = "প্রকৃতির শান্ত পরিবেশ। ধীরলয়ে বৃষ্টির ধারা এবং স্নিগ্ধ সবুজ আভা।",
        windowTintColor = 0xFF112211,
        dropletColor = 0xB3A7F3D0,
        dropletHighlightColor = 0xEEF0FDF4,
        rainDropDensity = 0.85f,
        lateralWindForce = 0f,
        slideFriction = 0.95f
    );

    companion object {
        // Legacy alias fallback
        val MONSOON = MONSOON_CITY
        val WINTER = WINTER_FROST
        val AUTUMN = VILLAGE_RAIN
    }
}
