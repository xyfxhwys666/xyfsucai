package com.example.material.service;

import com.example.material.model.Task;
import com.example.material.model.TaskAsset;
import com.example.material.repo.TaskAssetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

@Service
public class ResultComposer {
    private final TaskAssetMapper assets;
    private final Path storage;

    public ResultComposer(TaskAssetMapper assets, @Value("${app.storage-dir}") String storageDir) {
        this.assets = assets;
        this.storage = Paths.get(storageDir).toAbsolutePath().normalize();
    }

    public String create(Task task) throws IOException {
        List<TaskAsset> taskAssets = assets.findByTask(task.id());
        if (taskAssets.isEmpty()) throw new IOException("结果整理失败：任务没有可用素材");

        List<String> images = taskAssets.stream().map(this::dataUri).toList();
        String svg = switch (task.templateId()) {
            case "demo-avatar" -> avatar(images.getFirst());
            case "demo-collage" -> collage(images);
            default -> postcard(images);
        };

        Path resultDir = storage.resolve("results");
        Files.createDirectories(resultDir);
        Files.writeString(resultDir.resolve(task.id() + ".svg"), svg, StandardCharsets.UTF_8);
        return "/results/" + task.id() + ".svg";
    }

    private String dataUri(TaskAsset asset) {
        try {
            String fileName = Path.of(asset.fileUrl()).getFileName().toString();
            Path file = storage.resolve(fileName).normalize();
            if (!file.startsWith(storage)) throw new IOException("非法素材路径");
            return "data:" + asset.mimeType() + ";base64," + Base64.getEncoder().encodeToString(Files.readAllBytes(file));
        } catch (IOException error) {
            throw new IllegalStateException("结果整理失败：无法读取上传素材", error);
        }
    }

    private String avatar(String image) {
        return svgStart(1200, 1200) + """
          <defs><clipPath id="portrait"><circle cx="600" cy="540" r="430"/></clipPath></defs>
          <rect width="1200" height="1200" fill="#151713"/>
          <circle cx="600" cy="540" r="448" fill="#c8f078"/>
          <image href="%s" x="170" y="110" width="860" height="860" preserveAspectRatio="xMidYMid slice" clip-path="url(#portrait)"/>
          <text x="600" y="1060" text-anchor="middle" fill="#f4f4ee" font-family="Arial" font-size="46" font-weight="700">PROFILE PORTRAIT</text>
          <text x="600" y="1115" text-anchor="middle" fill="#9da097" font-family="Arial" font-size="22">Generated from your uploaded image</text>
        </svg>
        """.formatted(image);
    }

    private String postcard(List<String> images) {
        String first = images.getFirst();
        String second = images.size() > 1 ? images.get(1) : first;
        return svgStart(1400, 900) + """
          <defs><clipPath id="left"><rect x="0" y="0" width="900" height="900"/></clipPath></defs>
          <rect width="1400" height="900" fill="#f1eee7"/>
          <image href="%s" x="0" y="0" width="900" height="900" preserveAspectRatio="xMidYMid slice" clip-path="url(#left)"/>
          <image href="%s" x="965" y="95" width="340" height="340" preserveAspectRatio="xMidYMid slice"/>
          <rect x="948" y="78" width="374" height="374" fill="none" stroke="#20231e" stroke-width="3"/>
          <text x="970" y="565" fill="#20231e" font-family="Arial" font-size="28" letter-spacing="7">TRAVEL MEMORY</text>
          <line x1="970" y1="604" x2="1300" y2="604" stroke="#20231e" stroke-width="2"/>
          <text x="970" y="665" fill="#555a50" font-family="Arial" font-size="21">A simulated postcard composed</text>
          <text x="970" y="700" fill="#555a50" font-family="Arial" font-size="21">from your two uploaded photos.</text>
          <circle cx="1267" cy="790" r="48" fill="#c8f078"/>
          <text x="1267" y="800" text-anchor="middle" fill="#20231e" font-family="Arial" font-size="20" font-weight="700">MADE</text>
        </svg>
        """.formatted(first, second);
    }

    private String collage(List<String> images) {
        StringBuilder body = new StringBuilder(svgStart(1200, 1200));
        for (int index = 0; index < 4; index++) {
            String image = images.get(Math.min(index, images.size() - 1));
            int x = index % 2 * 600;
            int y = index / 2 * 600;
            body.append("<image href=\"").append(image).append("\" x=\"").append(x).append("\" y=\"").append(y)
                    .append("\" width=\"600\" height=\"600\" preserveAspectRatio=\"xMidYMid slice\"/>");
        }
        return body.append("<rect x=\"24\" y=\"24\" width=\"1152\" height=\"1152\" fill=\"none\" stroke=\"#f4f4ee\" stroke-width=\"12\"/></svg>").toString();
    }

    private String svgStart(int width, int height) {
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" + width + "\" height=\"" + height + "\" viewBox=\"0 0 " + width + " " + height + "\">";
    }
}
