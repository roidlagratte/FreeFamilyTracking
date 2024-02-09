package nonozi.freefamilytracking

class GetRequestModel (
    private var name: String, private var id: String
){
    override fun toString(): String {
        return "GetRequestModel{" +
                "name=" + name + '\'' +
                "id=" + id + '\'' +
                '}'
    }
}