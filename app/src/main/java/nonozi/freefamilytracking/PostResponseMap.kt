package nonozi.freefamilytracking

class PostResponseMap(
    private  val id: Int,
    public   val latitude: Double?,
    public  val longitude: Double?,
    private  val name: String
) {
    override fun toString(): String {
        return "id=$id\n" +
                "\t" + "name=$name\n" +
                "\t" + "latitude=$latitude\n" +
                "\t" + "longitude=$longitude"
    }
}
