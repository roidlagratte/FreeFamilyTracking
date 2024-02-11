package nonozi.freefamilytracking

data class PostRequestModel(
    val name: String,
    val groupName: String,
    val latitude: String,
    val longitude: String,
    val timestamp: String
)