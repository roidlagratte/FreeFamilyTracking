package nonozi.freefamilytracking

class PostResponseMap(
    var id: Int,
    var latitude: Double?,
    var longitude: Double?,
    var name: String
) {
    override fun toString(): String {
        return "id=$id\n" +
                "\t" + "name=$name\n" +
                "\t" + "latitude=$latitude\n" +
                "\t" + "longitude=$longitude"
    }
}
