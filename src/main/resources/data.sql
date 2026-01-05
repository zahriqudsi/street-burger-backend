-- Street Burger Initial Seed Data

-- Restaurant Info
INSERT INTO restaurant_info (name, address, phone, email, opening_hours, about_us, latitude, longitude, facebook_url, instagram_url, uber_eats_url, pickme_food_url) VALUES 
('Street Burger', 'No. 123, Galle Road, Colombo 03, Sri Lanka', '+94 11 234 5678', 'info@streetburger.lk', 'Monday - Sunday: 11:00 AM - 11:00 PM', 'Welcome to Street Burger - Where Sri Lankan Flavors Meet Gourmet Burgers! Nestled in the heart of Colombo, we bring you the finest seafood burgers inspired by the rich culinary heritage of Sri Lanka. Our chefs craft each burger with locally sourced ingredients and traditional spices that will take your taste buds on an unforgettable journey.', 6.9271, 79.8612, 'https://facebook.com/streetburgerlk', 'https://instagram.com/streetburgerlk', 'https://ubereats.com/streetburger', 'https://pickme.lk/food/streetburger');

-- Menu Categories
INSERT INTO menu_categories (name, name_si, name_ta, display_order, image_url) VALUES 
('Starters', 'ආරම්භක', 'ஆரம்பிகள்', 1, NULL),
('Signature Burgers', 'විශේෂ බර්ගර්', 'சிறப்பு பர்கர்கள்', 2, NULL),
('Seafood Specials', 'මුහුදු ආහාර විශේෂ', 'கடல் உணவு சிறப்புகள்', 3, NULL),
('Sides', 'පැති වට', 'பக்கவாட்டுகள்', 4, NULL),
('Beverages', 'පාන', 'பானங்கள்', 5, NULL),
('Desserts', 'අතුරුපස', 'இனிப்புகள்', 6, NULL);

-- Menu Items - Starters
INSERT INTO menu_items (category_id, title, description, price, is_available, is_popular, display_order) VALUES 
(1, 'Crispy Calamari', 'Golden fried calamari rings served with tangy tartar sauce', 850.00, true, true, 1),
(1, 'Prawn Tempura', 'Light and crispy battered prawns with sweet chili dip', 950.00, true, false, 2),
(1, 'Fish Cakes', 'Homemade Sri Lankan style fish cakes with onion sambol', 650.00, true, true, 3),
(1, 'Deviled Prawns', 'Spicy Sri Lankan style deviled prawns', 1100.00, true, false, 4);

-- Menu Items - Signature Burgers
INSERT INTO menu_items (category_id, title, description, price, is_available, is_popular, display_order) VALUES 
(2, 'Classic Street Burger', 'Juicy beef patty with cheese, lettuce, tomato, and our secret sauce', 1200.00, true, true, 1),
(2, 'Spicy Lankan Burger', 'Beef patty with fiery sambol, cheese, and crispy onions', 1350.00, true, true, 2),
(2, 'Chicken Royale', 'Crispy chicken breast with coleslaw and mayo', 1150.00, true, false, 3),
(2, 'Veggie Delight', 'Grilled vegetable patty with avocado and fresh greens', 950.00, true, false, 4);

-- Menu Items - Seafood Specials
INSERT INTO menu_items (category_id, title, description, price, is_available, is_popular, display_order) VALUES 
(3, 'Crab Burger', 'Premium crab meat patty with lime aioli and fresh lettuce', 1650.00, true, true, 1),
(3, 'Prawn Burger', 'Jumbo prawn patty with spicy mayo and pickled vegetables', 1450.00, true, true, 2),
(3, 'Fish Burger', 'Fresh catch of the day in a crispy coating with tartar sauce', 1250.00, true, false, 3),
(3, 'Lobster Burger', 'Luxurious lobster meat with garlic butter and microgreens', 2500.00, true, false, 4);

-- Menu Items - Sides
INSERT INTO menu_items (category_id, title, description, price, is_available, is_popular, display_order) VALUES 
(4, 'French Fries', 'Crispy golden fries with sea salt', 450.00, true, true, 1),
(4, 'Sweet Potato Fries', 'Crispy sweet potato fries with chipotle mayo', 550.00, true, false, 2),
(4, 'Onion Rings', 'Beer-battered onion rings', 500.00, true, false, 3),
(4, 'Coleslaw', 'Fresh creamy coleslaw', 350.00, true, false, 4);

-- Menu Items - Beverages
INSERT INTO menu_items (category_id, title, description, price, is_available, is_popular, display_order) VALUES 
(5, 'Fresh Lime Juice', 'Refreshing lime juice with mint', 350.00, true, true, 1),
(5, 'King Coconut', 'Fresh Sri Lankan king coconut', 250.00, true, true, 2),
(5, 'Mango Lassi', 'Creamy mango yogurt drink', 450.00, true, false, 3),
(5, 'Iced Coffee', 'Cold brew coffee with milk', 400.00, true, false, 4),
(5, 'Soft Drinks', 'Coca-Cola, Sprite, Fanta', 200.00, true, false, 5);

-- Menu Items - Desserts
INSERT INTO menu_items (category_id, title, description, price, is_available, is_popular, display_order) VALUES 
(6, 'Watalappan', 'Traditional Sri Lankan coconut custard', 450.00, true, true, 1),
(6, 'Chocolate Brownie', 'Warm brownie with vanilla ice cream', 550.00, true, false, 2),
(6, 'Ice Cream Sundae', 'Three scoops with chocolate sauce and nuts', 500.00, true, false, 3);

-- Sample Chefs
INSERT INTO chefs (name, title, bio, display_order, is_active) VALUES 
('Chef Kasun Silva', 'Head Chef', 'With over 15 years of culinary experience, Chef Kasun brings the authentic flavors of Sri Lanka to every dish. Trained in Colombo and Paris, he combines traditional techniques with modern presentation.', 1, true),
('Chef Nimal Perera', 'Sous Chef', 'Specializing in seafood, Chef Nimal has mastered the art of preparing the freshest catches from Sri Lanka''s coastal waters. His innovative seafood burgers are customer favorites.', 2, true),
('Chef Ayesha Fernando', 'Pastry Chef', 'Chef Ayesha creates delightful fusion desserts that blend Sri Lankan traditions with international flavors. Her Watalappan has won multiple local awards.', 3, true);

-- Sample Reviews
INSERT INTO reviews (phone_number, reviewer_name, rating, comment, is_approved, created_at) VALUES 
('+94771234567', 'Nuwan P.', 5, 'Best burgers in Colombo! The Crab Burger is absolutely amazing. Fresh ingredients and great service.', true, NOW()),
('+94772345678', 'Sachini R.', 5, 'Love the Sri Lankan twist on classic burgers. The Spicy Lankan Burger is my favorite - perfect level of heat!', true, NOW()),
('+94773456789', 'Ashan M.', 4, 'Great food and atmosphere. The seafood specials are fantastic. Will definitely come back!', true, NOW()),
('+94774567890', 'Dilini K.', 5, 'Perfect for a weekend lunch. The King Coconut is so refreshing and the Fish Burger was cooked to perfection.', true, NOW()),
('+94775678901', 'Ravindu S.', 4, 'Nice place with authentic Sri Lankan flavors. The Crispy Calamari starter is a must-try!', true, NOW());

-- Sample Gallery Images
INSERT INTO gallery_images (image_url, caption, display_order, is_active) VALUES 
('https://placehold.co/800x600/FF6B35/FFFFFF?text=Restaurant+Interior', 'Our cozy dining area', 1, true),
('https://placehold.co/800x600/FF6B35/FFFFFF?text=Signature+Burger', 'Our famous Crab Burger', 2, true),
('https://placehold.co/800x600/FF6B35/FFFFFF?text=Fresh+Seafood', 'Fresh catches daily', 3, true),
('https://placehold.co/800x600/FF6B35/FFFFFF?text=Happy+Customers', 'Happy customers enjoying their meal', 4, true),
('https://placehold.co/800x600/FF6B35/FFFFFF?text=Outdoor+Seating', 'Beautiful outdoor seating area', 5, true);

-- Admin User (password: admin123)
INSERT INTO users (phone_number, password, name, email, email_verified, role, created_at) VALUES 
('+94700000000', '$2a$10$N9qo8uLOickgx2ZMRZoHK.LCZvQz8Qh2.CqZj9TQ7.6Vy1sxzzWKm', 'Admin User', 'admin@streetburger.lk', true, 'ADMIN', NOW());
