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
            case "demo-postcard" -> postcard(images);
            case "demo-magazine-cover" -> magazineCover(images.getFirst());
            case "demo-scrapbook" -> scrapbook(images);
            case "demo-filmstrip" -> filmstrip(images);
            default -> magazineCover(images.getFirst());
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

    private String magazineCover(String image) {
        return svgStart(1000, 1400) + """
          <defs>
            <linearGradient id="editorialShade" x1="0" y1="0" x2="0" y2="1"><stop offset="20%%" stop-color="#0f1512" stop-opacity=".04"/><stop offset="100%%" stop-color="#0f1512" stop-opacity=".76"/></linearGradient>
            <clipPath id="coverPhoto"><rect x="38" y="176" width="924" height="1140" rx="2"/></clipPath>
          </defs>
          <rect width="1000" height="1400" fill="#f1efe7"/>
          <image href="%s" x="38" y="176" width="924" height="1140" preserveAspectRatio="xMidYMid slice" clip-path="url(#coverPhoto)"/>
          <rect x="38" y="176" width="924" height="1140" fill="url(#editorialShade)"/>
          <text x="42" y="125" fill="#121512" font-family="Arial" font-size="106" font-weight="800" letter-spacing="-4">FORM</text>
          <text x="788" y="70" fill="#121512" font-family="Arial" font-size="17" font-weight="700">ISSUE 07</text>
          <text x="788" y="99" fill="#596057" font-family="Arial" font-size="15">VISUAL CULTURE</text>
          <rect x="38" y="1090" width="455" height="226" fill="#d8ff62"/>
          <text x="72" y="1146" fill="#151713" font-family="Arial" font-size="18" font-weight="700" letter-spacing="4">THE IMAGE EDIT</text>
          <text x="69" y="1218" fill="#151713" font-family="Arial" font-size="60" font-weight="800">MAKE THE</text>
          <text x="69" y="1280" fill="#151713" font-family="Arial" font-size="60" font-weight="800">FRAME YOURS</text>
          <text x="690" y="1250" fill="#ffffff" font-family="Arial" font-size="18" font-weight="700" letter-spacing="3">ONE IMAGE</text>
          <text x="690" y="1282" fill="#e9e9e2" font-family="Arial" font-size="16">Editorial cover system</text>
          <line x1="38" y1="1351" x2="962" y2="1351" stroke="#121512" stroke-width="2"/>
        </svg>
        """.formatted(image);
    }

    private String scrapbook(List<String> images) {
        String first = images.getFirst();
        String second = images.get(Math.min(1, images.size() - 1));
        String third = images.get(Math.min(2, images.size() - 1));
        return svgStart(1400, 1000) + """
          <defs>
            <pattern id="paperGrid" width="36" height="36" patternUnits="userSpaceOnUse"><path d="M36 0H0V36" fill="none" stroke="#d7d0c2" stroke-width="1" opacity=".55"/></pattern>
            <filter id="paperShadow" x="-20%%" y="-20%%" width="140%%" height="140%%"><feDropShadow dx="0" dy="12" stdDeviation="12" flood-color="#3b3125" flood-opacity=".22"/></filter>
          </defs>
          <rect width="1400" height="1000" fill="#e9e1d2"/>
          <rect width="1400" height="1000" fill="url(#paperGrid)"/>
          <path d="M0 112 C270 72 430 144 690 94 S1110 78 1400 132" fill="none" stroke="#c6bba9" stroke-width="3" stroke-dasharray="10 14"/>
          <text x="78" y="92" fill="#342f29" font-family="Georgia" font-size="46" font-weight="700">weekend notes</text>
          <text x="1120" y="78" fill="#6d655b" font-family="Arial" font-size="18" letter-spacing="3">VOL. 03 / MEMORY</text>
          <g transform="translate(95 170) rotate(-5 250 300)" filter="url(#paperShadow)">
            <rect width="500" height="620" fill="#fffdf7"/>
            <image href="%s" x="28" y="28" width="444" height="490" preserveAspectRatio="xMidYMid slice"/>
            <text x="35" y="572" fill="#3b3730" font-family="Georgia" font-size="27" font-style="italic">the first frame</text>
          </g>
          <g transform="translate(570 115) rotate(4 210 260)" filter="url(#paperShadow)">
            <rect width="420" height="530" fill="#fffdf7"/>
            <image href="%s" x="25" y="25" width="370" height="400" preserveAspectRatio="xMidYMid slice"/>
            <text x="30" y="482" fill="#3b3730" font-family="Georgia" font-size="24">small discoveries</text>
          </g>
          <g transform="translate(945 405) rotate(-3 185 235)" filter="url(#paperShadow)">
            <rect width="370" height="470" fill="#fffdf7"/>
            <image href="%s" x="23" y="23" width="324" height="345" preserveAspectRatio="xMidYMid slice"/>
            <text x="28" y="423" fill="#3b3730" font-family="Georgia" font-size="22">keep this close</text>
          </g>
          <g opacity=".82"><rect x="265" y="142" width="190" height="45" fill="#e6c86d" transform="rotate(-4 360 164)"/><rect x="720" y="92" width="180" height="42" fill="#d98d79" transform="rotate(4 810 113)"/><rect x="1040" y="382" width="165" height="42" fill="#9bbd9c" transform="rotate(-3 1122 403)"/></g>
          <path d="M690 752 C760 705 825 705 890 750" fill="none" stroke="#ae5147" stroke-width="5" stroke-linecap="round"/>
          <text x="642" y="840" fill="#514a42" font-family="Arial" font-size="19" letter-spacing="3">THREE PHOTOS · ONE PAGE</text>
        </svg>
        """.formatted(first, second, third);
    }

    private String filmstrip(List<String> images) {
        String first = images.getFirst();
        String second = images.get(Math.min(1, images.size() - 1));
        String third = images.get(Math.min(2, images.size() - 1));
        return svgStart(1600, 1000) + """
          <defs>
            <linearGradient id="cinemaBg" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#111312"/><stop offset="100%%" stop-color="#252821"/></linearGradient>
            <filter id="cinemaTone"><feColorMatrix type="matrix" values=".82 .08 .05 0 0 .05 .78 .08 0 0 .04 .10 .68 0 0 0 0 0 1 0"/></filter>
          </defs>
          <rect width="1600" height="1000" fill="url(#cinemaBg)"/>
          <text x="84" y="102" fill="#f3f0e6" font-family="Arial" font-size="25" font-weight="700" letter-spacing="7">SEQUENCE / 03</text>
          <text x="1515" y="102" text-anchor="end" fill="#9da296" font-family="Arial" font-size="18">A STORY IN THREE FRAMES</text>
          <rect x="62" y="155" width="1476" height="610" rx="6" fill="#080908"/>
          <g fill="#ede7d6"><rect x="86" y="174" width="52" height="28" rx="5"/><rect x="176" y="174" width="52" height="28" rx="5"/><rect x="266" y="174" width="52" height="28" rx="5"/><rect x="356" y="174" width="52" height="28" rx="5"/><rect x="446" y="174" width="52" height="28" rx="5"/><rect x="536" y="174" width="52" height="28" rx="5"/><rect x="626" y="174" width="52" height="28" rx="5"/><rect x="716" y="174" width="52" height="28" rx="5"/><rect x="806" y="174" width="52" height="28" rx="5"/><rect x="896" y="174" width="52" height="28" rx="5"/><rect x="986" y="174" width="52" height="28" rx="5"/><rect x="1076" y="174" width="52" height="28" rx="5"/><rect x="1166" y="174" width="52" height="28" rx="5"/><rect x="1256" y="174" width="52" height="28" rx="5"/><rect x="1346" y="174" width="52" height="28" rx="5"/><rect x="1436" y="174" width="52" height="28" rx="5"/></g>
          <g filter="url(#cinemaTone)"><image href="%s" x="92" y="230" width="448" height="445" preserveAspectRatio="xMidYMid slice"/><image href="%s" x="576" y="230" width="448" height="445" preserveAspectRatio="xMidYMid slice"/><image href="%s" x="1060" y="230" width="448" height="445" preserveAspectRatio="xMidYMid slice"/></g>
          <g fill="#ede7d6"><rect x="86" y="715" width="52" height="28" rx="5"/><rect x="176" y="715" width="52" height="28" rx="5"/><rect x="266" y="715" width="52" height="28" rx="5"/><rect x="356" y="715" width="52" height="28" rx="5"/><rect x="446" y="715" width="52" height="28" rx="5"/><rect x="536" y="715" width="52" height="28" rx="5"/><rect x="626" y="715" width="52" height="28" rx="5"/><rect x="716" y="715" width="52" height="28" rx="5"/><rect x="806" y="715" width="52" height="28" rx="5"/><rect x="896" y="715" width="52" height="28" rx="5"/><rect x="986" y="715" width="52" height="28" rx="5"/><rect x="1076" y="715" width="52" height="28" rx="5"/><rect x="1166" y="715" width="52" height="28" rx="5"/><rect x="1256" y="715" width="52" height="28" rx="5"/><rect x="1346" y="715" width="52" height="28" rx="5"/><rect x="1436" y="715" width="52" height="28" rx="5"/></g>
          <text x="92" y="818" fill="#d8ff62" font-family="Arial" font-size="18" font-weight="700">FRAME 01</text><text x="576" y="818" fill="#d8ff62" font-family="Arial" font-size="18" font-weight="700">FRAME 02</text><text x="1060" y="818" fill="#d8ff62" font-family="Arial" font-size="18" font-weight="700">FRAME 03</text>
          <text x="92" y="866" fill="#f3f0e6" font-family="Georgia" font-size="28">An opening image</text><text x="576" y="866" fill="#f3f0e6" font-family="Georgia" font-size="28">A change in rhythm</text><text x="1060" y="866" fill="#f3f0e6" font-family="Georgia" font-size="28">The final memory</text>
          <line x1="92" y1="914" x2="1508" y2="914" stroke="#5e6359" stroke-width="2"/>
          <text x="92" y="954" fill="#9da296" font-family="Arial" font-size="17" letter-spacing="3">YOUR UPLOADS · FIXED NARRATIVE LAYOUT · NO AI SERVICE</text>
        </svg>
        """.formatted(first, second, third);
    }

    private String svgStart(int width, int height) {
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" + width + "\" height=\"" + height + "\" viewBox=\"0 0 " + width + " " + height + "\">";
    }
}
