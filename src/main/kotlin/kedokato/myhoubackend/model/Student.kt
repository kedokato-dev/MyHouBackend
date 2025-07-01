package kedokato.myhoubackend.model

data class Student (
    val id: String? = null,
    val name: String? = null,
    val birthDate: String? = null,
    val sex : String? = null,
    val address: String? = null,
    val phoneParent: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val addressNow: String? = null,

    val programType : String? = null,
    val faculty: String? = null,
    val courseYear : String? = null,
    val major : String? = null,
    val className: String? = null,

)