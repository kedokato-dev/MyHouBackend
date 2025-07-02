package kedokato.myhoubackend.parser

import kedokato.myhoubackend.model.Student
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component

@Component
class StudentInfoParser {

    fun parseStudentInfo(doc: Document): Student {
        val studentId = doc.select("#txtMa_sv")?.attr("value") ?:"Không có thông tin"
        val studentName = doc.select("#txtHo_ten")?.attr("value")?:"Không xác định"
        val birthDate = doc.select("#txtNgay_sinh")?.attr("value")?: "Không có thông tin"
        val sex = doc.select("#txtGioi_tinh")?.attr("value") ?: "Không xác định"
        val address = doc.select("#txtNoi_sinh")?.attr("value")?: "Không có thông tin"
        val phoneParent = doc.select("#txtDien_thoai_nr")?.attr("value") ?: "Không có thông tin"
        val phone = doc.select("#txtDien_thoai_cn")?.attr("value")?: "Không có thông tin"
        val email = doc.select("#txtEmail")?.attr("value")?: "Không có thông tin"
        val addressNow = doc.select("#txtNoi_o_hien_nay")?.attr("value")?: "Không có thông tin"

        val programType = doc.select("#txtTen_he")?.attr("value")?: "Không có thông tin"
        val faculty = doc.select("#txtTen_khoa")?.attr("value")?: "Không có thông tin"
        val courseYear = doc.select("#txtKhoa_hoc")?.attr("value")?: "Không có thông tin"
        val major = doc.select("#txtTen_chuyen_nganh")?.attr("value")?: "Không có thông tin"
        val className = doc.select("#txtTen_lop")?.attr("value")?: "Không có thông tin"

        return Student(
            id = studentId,
            name = studentName,
            birthDate = birthDate,
            sex = sex,
            address = address,
            phoneParent = phoneParent,
            phone = phone,
            email = email,
            addressNow = addressNow,
            programType = programType,
            faculty = faculty,
            courseYear = courseYear,
            major = major,
            className = className
        )
    }
}