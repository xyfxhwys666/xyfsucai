INSERT IGNORE INTO templates(id,title,category,cover_url,description,required_image_count,accepted_mime_types,max_file_size_bytes) VALUES
('demo-postcard','旅行明信片','创意图片','/demo/sleeping-girl.jpg','上传两张图片，模拟生成一张有旅行感的明信片。',2,'image/jpeg,image/png',5242880),
('demo-collage','四格拼贴','照片拼贴','/demo/witch.jpg','上传四张图片，模拟整理成四格拼贴海报。',4,'image/jpeg,image/png,image/webp',5242880),
('demo-avatar','社交头像','个人形象','/demo/blue-girl.jpg','上传一张图片，模拟生成圆形头像。',1,'image/jpeg,image/png',5242880)
ON DUPLICATE KEY UPDATE title=VALUES(title),category=VALUES(category),cover_url=VALUES(cover_url),description=VALUES(description),required_image_count=VALUES(required_image_count),accepted_mime_types=VALUES(accepted_mime_types),max_file_size_bytes=VALUES(max_file_size_bytes);
