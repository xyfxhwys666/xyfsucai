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


def magazine_cover() -> None:
    canvas = Image.new("RGB", SIZE, "#111410")
    cover = Image.new("RGB", (470, 664), "#f1efe7")
    cover.paste(fit("showcase-sources/editorial-portrait.jpg", (436, 530)), (17, 88))
    draw_cover = ImageDraw.Draw(cover)
    draw_cover.text((18, 8), "FORM", fill="#121512", font=font(64, True))
    draw_cover.text((366, 17), "ISSUE 07", fill="#121512", font=font(14, True))
    draw_cover.rectangle((17, 465, 252, 618), fill="#d8ff62")
    draw_cover.text((34, 484), "THE IMAGE EDIT", fill="#151713", font=font(14, True))
    draw_cover.text((32, 520), "MAKE THE", fill="#151713", font=font(32, True))
    draw_cover.text((32, 558), "FRAME YOURS", fill="#151713", font=font(28, True))
    canvas.paste(cover, (155, 45))
    draw = ImageDraw.Draw(canvas)
    draw.text((655, 230), "EDITORIAL SYSTEM", fill="#d8ff62", font=font(21, True))
    draw.text((652, 278), "ONE IMAGE.", fill="#f4f4ee", font=font(52, True))
    draw.text((652, 338), "STRONG COVER.", fill="#f4f4ee", font=font(47, True))
    draw.line((655, 420, 1110, 420), fill="#6f746a", width=2)
    draw.text((655, 462), "Masthead / issue / hierarchy", fill="#aeb2a8", font=font(23))
    draw.text((655, 500), "and a fixed accent panel", fill="#aeb2a8", font=font(23))
    save(canvas, "result-demo-magazine-cover.jpg")


def scrapbook() -> None:
    canvas = Image.new("RGB", SIZE, "#e9e1d2")
    draw = ImageDraw.Draw(canvas)
    for x in range(0, 1200, 30):
        draw.line((x, 0, x, 756), fill="#d9d0c1", width=1)
    for y in range(0, 756, 30):
        draw.line((0, y, 1200, y), fill="#d9d0c1", width=1)
    draw.text((55, 38), "weekend notes", fill="#342f29", font=font(38, True))
    cards = [
        (fit("showcase-sources/memory-blossom.jpg", (320, 310)), (80, 145), -5, "the first frame"),
        (fit("showcase-sources/memory-cave.jpg", (275, 250)), (445, 104), 4, "small discoveries"),
        (fit("showcase-sources/story-rain.jpg", (260, 235)), (785, 335), -3, "keep this close"),
    ]
    for scene, position, angle, caption in cards:
        card = Image.new("RGB", (scene.width + 34, scene.height + 94), "#fffdf7")
        card.paste(scene, (17, 17))
        ImageDraw.Draw(card).text((22, scene.height + 40), caption, fill="#3b3730", font=font(19))
        rotated = card.rotate(angle, expand=True, resample=Image.Resampling.BICUBIC, fillcolor="#e9e1d2")
        canvas.paste(rotated, position)
    draw = ImageDraw.Draw(canvas)
    draw.rectangle((185, 121, 315, 150), fill="#e6c86d")
    draw.rectangle((525, 87, 650, 114), fill="#d98d79")
    draw.rectangle((865, 314, 980, 341), fill="#9bbd9c")
    draw.text((815, 78), "THREE PHOTOS / ONE PAGE", fill="#6d655b", font=font(18, True))
    draw.arc((520, 570, 740, 730), 205, 330, fill="#ae5147", width=5)
    save(canvas, "result-demo-scrapbook.jpg")


def filmstrip() -> None:
    canvas = Image.new("RGB", SIZE, "#171a17")
    draw = ImageDraw.Draw(canvas)
    draw.text((55, 42), "SEQUENCE / 03", fill="#f3f0e6", font=font(24, True))
    draw.text((955, 48), "A STORY IN THREE FRAMES", fill="#9da296", font=font(16))
    draw.rounded_rectangle((45, 125, 1155, 590), radius=8, fill="#080908")
    for x in range(65, 1140, 66):
        draw.rounded_rectangle((x, 140, x + 38, 161), radius=4, fill="#ede7d6")
        draw.rounded_rectangle((x, 554, x + 38, 575), radius=4, fill="#ede7d6")
    slots = [(68, 183), (440, 183), (812, 183)]
    scenes = [
        fit("showcase-sources/story-rain.jpg", (320, 345)),
        fit("showcase-sources/story-time.jpg", (320, 345)),
        fit("showcase-sources/story-ice.jpg", (320, 345)),
    ]
    for index, (point, scene) in enumerate(zip(slots, scenes)):
        canvas.paste(scene, point)
        draw.text((point[0], 620), f"FRAME 0{index + 1}", fill="#d8ff62", font=font(16, True))
    draw.text((68, 666), "Opening image", fill="#f3f0e6", font=font(23))
    draw.text((440, 666), "Change in rhythm", fill="#f3f0e6", font=font(23))
    draw.text((812, 666), "The final memory", fill="#f3f0e6", font=font(23))
    draw.line((68, 718, 1132, 718), fill="#5e6359", width=2)
    save(canvas, "result-demo-filmstrip.jpg")


if __name__ == "__main__":
    avatar()
    collage()
    postcard()
    magazine_cover()
    scrapbook()
    filmstrip()
