package com.example.dessertclicker.data

data class WordItem(
    val word: String,
    val definition: String
)

object WordsData {
    val dailyVocabulary = listOf(
        WordItem("Achievement", "ความสำเร็จ"),
        WordItem("Benevolent", "เมตตา"),
        WordItem("Candid", "ตรงไปตรงมา"),
        WordItem("Diligent", "ขยัน"),
        WordItem("Eloquent", "มีวาทศิลป์"),
        WordItem("Frugal", "ประหยัด"),
        WordItem("Genuine", "แท้จริง"),
        WordItem("Humble", "ถ่อมตัว"),
        WordItem("Innovative", "นวัตกรรม"),
        WordItem("Jovial", "ร่าเริง"),
        WordItem("Knowledge", "ความรู้"),
        WordItem("Lucrative", "ที่ทำกำไรได้ดี"),
        WordItem("Meticulous", "พิถีพิถัน"),
        WordItem("Nurture", "บ่มเพาะ"),
        WordItem("Optimistic", "มองโลกในแง่ดี"),
        WordItem("Punctual", "ตรงต่อเวลา"),
        WordItem("Resilient", "ยืดหยุ่น/ฟื้นตัวเร็ว"),
        WordItem("Spontaneous", "เกิดขึ้นเองตามธรรมชาติ"),
        WordItem("Thrive", "เจริญรุ่งเรือง"),
        WordItem("Versatile", "เอนกประสงค์")
    )
}
