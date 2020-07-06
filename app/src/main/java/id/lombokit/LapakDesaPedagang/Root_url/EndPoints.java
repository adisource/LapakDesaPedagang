package id.lombokit.LapakDesaPedagang.Root_url;

public class EndPoints {
    private static final String ROOT_URL = "https://elades.lomboktimurkab.go.id/";
    public static final String UPLOAD_URL = ROOT_URL + "insert_barang.php?apicall=" + "uploadpic";
    public static final String UPDATE_URL = ROOT_URL + "update_photo_supplier.php?apicall=" + "uploadpic";
    public static final String INSERT_BARANG = ROOT_URL + "Barang_Api";

    public static final String ROOT_URL2 = "https://elades.lomboktimurkab.go.id/";
    public static final String URL_SEND_SINGLE_PUSH = ROOT_URL2 + "backend/folder_notifikasi/sendSinglePush.php";
    public static final String URL_REGISTERED_TOKEN = ROOT_URL2 + "backend_suplayer/folder_notifikasi/RegisterDeviceSupplier.php";
    public static final String URL_VIEW_KURIR = ROOT_URL + "backend_suplayer/view_kurir.php";
    public static final String URL_VIEW_NOTIFIKASI = ROOT_URL + "backend_suplayer/view_notifikasi.php";
    public static final String URL_DELETE_NOTIFIKASI = ROOT_URL + "backend_suplayer/delete_notifikasi.php";
    public static final String URL_BADGE_NOTIFIKASI = ROOT_URL + "backend_suplayer/view_badge.php";
}
