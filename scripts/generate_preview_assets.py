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


if __name__ == "__main__":
    avatar()
    collage()
    postcard()
