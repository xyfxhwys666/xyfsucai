DELETE FROM templates
WHERE id IN ('demo-dream-poster', 'demo-rainy-cinema', 'demo-fantasy-scene');

INSERT IGNORE INTO templates(id,title,category,cover_url,description,required_image_count,accepted_mime_types,max_file_size_bytes) VALUES
('demo-postcard','旅行明信片','创意图片','/demo/result-demo-postcard.jpg','上传两张图片，模拟生成一张有旅行感的明信片。',2,'image/jpeg,image/png',5242880),
('demo-collage','四格拼贴','照片拼贴','/demo/result-demo-collage.jpg','上传四张图片，模拟整理成四格拼贴海报。',4,'image/jpeg,image/png,image/webp',5242880),
('demo-avatar','社交头像','个人形象','/demo/result-demo-avatar.jpg','上传一张图片，生成带圆形裁切和身份标题的头像效果。',1,'image/jpeg,image/png',5242880),
('demo-magazine-cover','编辑杂志封面','编辑设计','/demo/result-demo-magazine-cover.jpg','上传一张图片，按固定刊头、期号、标题层级和强调色块生成编辑杂志封面。',1,'image/jpeg,image/png,image/webp',5242880),
('demo-scrapbook','复古剪贴簿','照片拼贴','/demo/result-demo-scrapbook.jpg','上传三张图片，按固定纸张背景、拍立得相框、旋转角度和胶带元素生成剪贴簿。',3,'image/jpeg,image/png,image/webp',5242880),
('demo-filmstrip','电影胶片故事板','叙事画面','/demo/result-demo-filmstrip.jpg','上传三张图片，按固定胶片孔、连续分镜、编号和字幕区域生成电影故事板。',3,'image/jpeg,image/png,image/webp',5242880)
ON DUPLICATE KEY UPDATE title=VALUES(title),category=VALUES(category),cover_url=VALUES(cover_url),description=VALUES(description),required_image_count=VALUES(required_image_count),accepted_mime_types=VALUES(accepted_mime_types),max_file_size_bytes=VALUES(max_file_size_bytes);
