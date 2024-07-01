import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import java.net.URL;
//测试打开一张图片
public class openCVTest {
    public static void main(String[] args) {
        loadDll();
        Mat inputImage = Imgcodecs.imread("src/main/resources/opencvImgs/bus.jpg",1);
        if (!inputImage.empty())
        {
            HighGui.namedWindow("test");
            HighGui.imshow("test", inputImage);
            HighGui.waitKey();
            HighGui.destroyAllWindows();
            System.exit(0);
        } else {
            System.out.println("file open error!");
        }
    }
    public static void loadDll() {
        // 解决awt报错问题
        System.setProperty("java.awt.headless", "false");
        System.out.println(System.getProperty("java.library.path"));
        // 加载动态库
        URL url = ClassLoader.getSystemResource("dlls/opencv_java452.dll");
        System.load(url.getPath());
    }
}
