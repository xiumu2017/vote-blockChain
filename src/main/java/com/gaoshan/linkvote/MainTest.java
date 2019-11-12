package com.gaoshan.linkvote;

import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;

public class MainTest {
    public static void main(String[] args) {
        long s = System.currentTimeMillis();
        try {
            Thumbnails.of(new File("E:\\pic\\2019-11-06\\微信图片_20191014135159.jpg")).scale(1f).outputQuality(0.5f).toFile("E:\\1.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        long e = System.currentTimeMillis();
        System.out.println(e - s);
    }
}
