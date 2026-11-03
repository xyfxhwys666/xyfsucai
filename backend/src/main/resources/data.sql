INSERT IGNORE INTO templates(id,title,category,cover_url,description,required_image_count,accepted_mime_types,max_file_size_bytes) VALUES
('demo-postcard','旅行明信片','创意图片','/demo/result-demo-postcard.jpg','上传两张图片，模拟生成一张有旅行感的明信片。',2,'image/jpeg,image/png',5242880),
('demo-collage','四格拼贴','照片拼贴','/demo/result-demo-collage.jpg','上传四张图片，模拟整理成四格拼贴海报。',4,'image/jpeg,image/png,image/webp',5242880),
('demo-avatar','社交头像','个人形象','/demo/result-demo-avatar.jpg','上传一张图片，生成带圆形裁切和身份标题的头像效果。',1,'image/jpeg,image/png',5242880),
('demo-dream-poster','梦境海报','艺术海报','/demo/result-demo-dream-poster.jpg','上传一张图片，按固定电影标题、渐暗蒙版和海报边框生成竖版梦境海报。',1,'image/jpeg,image/png,image/webp',5242880),
('demo-rainy-cinema','雨夜电影感','氛围写真','/demo/result-demo-rainy-cinema.jpg','上传一张图片，按固定宽银幕黑边、冷色调和电影字幕生成画面。',1,'image/jpeg,image/png,image/webp',5242880),
('demo-fantasy-scene','冰川幻想','奇幻场景','/demo/result-demo-fantasy-scene.jpg','上传两张图片，按固定主场景、档案插图和冷色标题版式组合封面。',2,'image/jpeg,image/png,image/webp',5242880)
ON DUPLICATE KEY UPDATE title=VALUES(title),category=VALUES(category),cover_url=VALUES(cover_url),description=VALUES(description),required_image_count=VALUES(required_image_count),accepted_mime_types=VALUES(accepted_mime_types),max_file_size_bytes=VALUES(max_file_size_bytes);
