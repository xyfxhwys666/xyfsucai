from pathlib import Path

from PIL import Image, ImageDraw, ImageFont, ImageOps


ROOT = Path(__file__).resolve().parents[1]
DEMO = ROOT / "frontend" / "public" / "demo"
SIZE = (1200, 756)


def font(size: int, bold: bool = False) -> ImageFont.FreeTypeFont:
    name = "arialbd.ttf" if bold else "arial.ttf"
    return ImageFont.truetype(str(Path("C:/Windows/Fonts") / name), size)


def fit(name: str, size: tuple[int, int]) -> Image.Image:
    return ImageOps.fit(Image.open(DEMO / name).convert("RGB"), size, method=Image.Resampling.LANCZOS)


def save(image: Image.Image, name: str) -> None:
    image.save(DEMO / name, "JPEG", quality=91, optimize=True, progressive=True)


def avatar() -> None:
    canvas = Image.new("RGB", SIZE, "#111410")
    draw = ImageDraw.Draw(canvas)
    portrait = fit("blue-girl.jpg", (520, 520))
    mask = Image.new("L", portrait.size, 0)
    ImageDraw.Draw(mask).ellipse((0, 0, 519, 519), fill=255)
    draw.ellipse((74, 118, 626, 670), fill="#c8f078")
    canvas.paste(portrait, (90, 134), mask)
    draw.text((710, 245), "PROFILE / 01", fill="#c8f078", font=font(25))
    draw.text((708, 304), "NEW IDENTITY", fill="#f4f4ee", font=font(54, True))
    draw.line((710, 390, 1110, 390), fill="#74796e", width=2)
    draw.text((710, 425), "Circular portrait composition", fill="#aeb2a8", font=font(23))
    draw.text((710, 462), "from your uploaded image", fill="#aeb2a8", font=font(23))
    save(canvas, "result-demo-avatar.jpg")


def collage() -> None:
    canvas = Image.new("RGB", SIZE, "#11130f")
    slots = [(28, 28), (614, 28), (28, 392), (614, 392)]
    names = ["witch.jpg", "sleeping-girl.jpg", "neon-clock.jpg", "blue-girl.jpg"]
    for name, point in zip(names, slots):
        canvas.paste(fit(name, (558, 336)), point)
    draw = ImageDraw.Draw(canvas)
    draw.rectangle((28, 28, 1172, 728), outline="#f4f4ee", width=7)
    draw.rectangle((365, 308, 835, 448), fill="#11130f")
    draw.text((600, 338), "FOUR MOMENTS", anchor="ma", fill="#c8f078", font=font(22))
    draw.text((600, 383), "VISUAL ARCHIVE", anchor="ma", fill="white", font=font(38, True))
    save(canvas, "result-demo-collage.jpg")


def postcard() -> None:
    canvas = Image.new("RGB", SIZE, "#f0eee7")
    canvas.paste(fit("sleeping-girl.jpg", (760, 756)), (0, 0))
    canvas.paste(fit("witch.jpg", (276, 220)), (838, 86))
    draw = ImageDraw.Draw(canvas)
    draw.rectangle((822, 70, 1130, 322), outline="#20231e", width=4)
    draw.text((825, 420), "TRAVEL MEMORY", fill="#20231e", font=font(25))
    draw.line((825, 464, 1122, 464), fill="#20231e", width=2)
    draw.text((825, 505), "Two uploaded photos,", fill="#596056", font=font(22))
    draw.text((825, 540), "one personal postcard.", fill="#596056", font=font(22))
    draw.ellipse((1010, 625, 1110, 725), fill="#c8f078")
    draw.text((1060, 662), "MADE", anchor="ma", fill="#20231e", font=font(18, True))
    save(canvas, "result-demo-postcard.jpg")


def dream_poster() -> None:
    canvas = Image.new("RGB", SIZE, "#10120f")
    poster = Image.new("RGB", (520, 676))
    poster_draw = ImageDraw.Draw(poster)
    for y in range(676):
        ratio = y / 675
        color = (int(50 + 65 * ratio), int(35 + 18 * ratio), int(95 - 35 * ratio))
        poster_draw.line((0, y, 520, y), fill=color)
    poster_draw.ellipse((315, 82, 435, 202), fill="#f5dca5")
    poster_draw.polygon([(0, 420), (115, 270), (220, 410), (330, 245), (520, 430), (520, 676), (0, 676)], fill="#22243d")
    poster_draw.polygon([(0, 500), (150, 370), (280, 505), (405, 340), (520, 450), (520, 676), (0, 676)], fill="#131827")
    for x, y in [(72, 92), (146, 165), (238, 105), (286, 210), (458, 65), (390, 255)]:
        poster_draw.ellipse((x, y, x + 5, y + 5), fill="white")
    canvas.paste(poster, (70, 40))
    draw = ImageDraw.Draw(canvas)
    draw.rectangle((88, 58, 572, 698), outline="#f4f1e8", width=3)
    draw.text((112, 570), "DREAMSCAPE", fill="white", font=font(23))
    draw.text((110, 612), "BEYOND LIGHT", fill="white", font=font(39, True))
    draw.text((680, 258), "FIXED POSTER", fill="#c8f078", font=font(23))
    draw.text((678, 307), "ONE IMAGE", fill="#f4f4ee", font=font(52, True))
    draw.line((680, 380, 1105, 380), fill="#6f746a", width=2)
    draw.text((680, 420), "Border / title / gradient", fill="#aeb2a8", font=font(24))
    draw.text((680, 457), "applied to your upload", fill="#aeb2a8", font=font(24))
    save(canvas, "result-demo-dream-poster.jpg")


def rainy_cinema() -> None:
    canvas = Image.new("RGB", SIZE)
    draw = ImageDraw.Draw(canvas)
    for y in range(756):
        ratio = y / 755
        draw.line((0, y, 1200, y), fill=(int(10 + 18 * ratio), int(22 + 35 * ratio), int(42 + 55 * ratio)))
    buildings = [(0, 205, 145, 648), (122, 155, 300, 648), (285, 248, 430, 648), (410, 125, 575, 648), (560, 220, 710, 648), (695, 90, 875, 648), (850, 185, 1010, 648), (995, 130, 1200, 648)]
    for index, box in enumerate(buildings):
        draw.rectangle(box, fill="#101c2c" if index % 2 else "#15273a")
        for wx in range(box[0] + 18, box[2] - 10, 34):
            for wy in range(box[1] + 28, box[3] - 24, 42):
                if (wx + wy + index) % 3:
                    draw.rectangle((wx, wy, wx + 12, wy + 18), fill="#78cbd1")
    draw.rectangle((0, 610, 1200, 648), fill="#1e4051")
    for x in range(0, 1200, 70):
        draw.line((x, 650, x - 45, 730), fill="#4d8ca1", width=3)
    draw.rectangle((0, 0, 1200, 108), fill="#090a0d")
    draw.rectangle((0, 648, 1200, 756), fill="#090a0d")
    for x in range(-120, 1320, 78):
        draw.line((x, 120, x - 120, 635), fill="#bed7ee", width=2)
    draw.text((72, 145), "NIGHT ARCHIVE / 06", fill="#eef0ee", font=font(20))
    draw.text((70, 548), "AFTER THE RAIN", fill="white", font=font(55, True))
    draw.text((72, 607), "Fixed cinematic crop, tint and captions", fill="#d0d5dc", font=font(22))
    save(canvas, "result-demo-rainy-cinema.jpg")


def fantasy_scene() -> None:
    canvas = Image.new("RGB", SIZE)
    draw = ImageDraw.Draw(canvas)
    for y in range(756):
        ratio = y / 755
        draw.line((0, y, 1200, y), fill=(int(16 + 42 * ratio), int(48 + 75 * ratio), int(78 + 82 * ratio)))
    draw.ellipse((850, 62, 1035, 247), fill="#d9f7ff")
    draw.polygon([(0, 520), (175, 245), (330, 510), (515, 180), (720, 520), (920, 270), (1200, 530), (1200, 756), (0, 756)], fill="#b9dce7")
    draw.polygon([(0, 575), (225, 360), (390, 560), (590, 300), (760, 570), (980, 390), (1200, 550), (1200, 756), (0, 756)], fill="#648ca8")
    draw.polygon([(0, 620), (220, 540), (450, 610), (690, 510), (930, 600), (1200, 530), (1200, 756), (0, 756)], fill="#d4f4f8")
    inset = Image.new("RGB", (250, 250), "#153c59")
    inset_draw = ImageDraw.Draw(inset)
    inset_draw.polygon([(125, 18), (154, 94), (232, 125), (154, 156), (125, 232), (96, 156), (18, 125), (96, 94)], fill="#9ce8f4")
    inset_draw.polygon([(125, 54), (143, 107), (196, 125), (143, 143), (125, 196), (107, 143), (54, 125), (107, 107)], fill="#e2fbff")
    canvas.paste(inset, (862, 88))
    draw.rectangle((842, 68, 1132, 358), outline="#b8efff", width=4)
    draw.rectangle((45, 500, 735, 704), fill="#17394d")
    draw.text((66, 535), "EXPEDITION FILE", fill="#c8eff8", font=font(23))
    draw.text((64, 582), "FROZEN HORIZON", fill="white", font=font(54, True))
    draw.text((66, 653), "Main image + second image archive panel", fill="#d2edf4", font=font(22))
    save(canvas, "result-demo-fantasy-scene.jpg")


if __name__ == "__main__":
    avatar()
    collage()
    postcard()
    dream_poster()
    rainy_cinema()
    fantasy_scene()
