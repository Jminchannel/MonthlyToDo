#!/usr/bin/env python3
"""
MonthlyToDo Feature Graphic Generator
Generates a 1024x500px PNG feature graphic for Google Play Store
"""

try:
    from PIL import Image, ImageDraw, ImageFont
    import os
    
    def create_feature_graphic():
        # Create canvas - 1024x500px as required by Google Play
        width, height = 1024, 500
        img = Image.new('RGB', (width, height), color='white')
        draw = ImageDraw.Draw(img)
        
        # Create gradient background
        for i in range(height):
            # Purple to pink gradient
            r = int(102 + (240 - 102) * i / height)  # 667eea to f093fb
            g = int(126 + (147 - 126) * i / height)
            b = int(234 + (251 - 234) * i / height)
            draw.rectangle([(0, i), (width, i+1)], fill=(r, g, b))
        
        # Use default font (more compatible)
        try:
            # Try to use system fonts
            title_font = ImageFont.truetype("arial.ttf", 52) if os.name == 'nt' else ImageFont.load_default()
            subtitle_font = ImageFont.truetype("arial.ttf", 28) if os.name == 'nt' else ImageFont.load_default()
            feature_font = ImageFont.truetype("arial.ttf", 20) if os.name == 'nt' else ImageFont.load_default()
            small_font = ImageFont.truetype("arial.ttf", 16) if os.name == 'nt' else ImageFont.load_default()
        except:
            # Fallback to default font
            title_font = ImageFont.load_default()
            subtitle_font = ImageFont.load_default() 
            feature_font = ImageFont.load_default()
            small_font = ImageFont.load_default()
        
        # Phone mockup on the left
        phone_x, phone_y = 60, 60
        phone_w, phone_h = 300, 380
        
        # Phone frame (dark)
        draw.rounded_rectangle(
            [(phone_x, phone_y), (phone_x + phone_w, phone_y + phone_h)],
            radius=25,
            fill=(40, 40, 40)
        )
        
        # Phone screen (white)
        margin = 20
        draw.rounded_rectangle(
            [(phone_x + margin, phone_y + margin), 
             (phone_x + phone_w - margin, phone_y + phone_h - margin)],
            radius=20,
            fill=(255, 255, 255)
        )
        
        # App interface mockup
        screen_x = phone_x + margin + 10
        screen_y = phone_y + margin + 10
        
        # Header
        draw.rectangle([(screen_x, screen_y), (screen_x + 240, screen_y + 50)], fill=(248, 250, 252))
        draw.text((screen_x + 120, screen_y + 25), "MonthlyToDo", fill=(45, 55, 72), font=small_font, anchor="mm")
        
        # Calendar grid mockup
        for row in range(5):
            for col in range(7):
                x = screen_x + 15 + col * 32
                y = screen_y + 70 + row * 32
                
                # Some circles are green (completed tasks)
                if (row + col) % 4 == 0:
                    color = (34, 197, 94)  # Green
                    text_color = (255, 255, 255)
                else:
                    color = (229, 231, 235)  # Gray
                    text_color = (75, 85, 99)
                
                draw.ellipse([(x-12, y-12), (x+12, y+12)], fill=color)
                draw.text((x, y), str((row * 7 + col + 1) % 32 + 1), fill=text_color, font=small_font, anchor="mm")
        
        # Task list area
        task_area_y = screen_y + 240
        draw.rectangle([(screen_x, task_area_y), (screen_x + 240, task_area_y + 80)], fill=(243, 244, 246))
        
        # Task items with priority dots
        draw.ellipse([(screen_x + 15, task_area_y + 15), (screen_x + 25, task_area_y + 25)], fill=(239, 68, 68))  # Red
        draw.text((screen_x + 35, task_area_y + 20), "High Priority Task", fill=(31, 41, 55), font=small_font)
        
        draw.ellipse([(screen_x + 15, task_area_y + 40), (screen_x + 25, task_area_y + 50)], fill=(34, 197, 94))  # Green
        draw.text((screen_x + 35, task_area_y + 45), "Completed Task", fill=(31, 41, 55), font=small_font)
        
        draw.ellipse([(screen_x + 15, task_area_y + 65), (screen_x + 25, task_area_y + 75)], fill=(251, 146, 60))  # Orange
        draw.text((screen_x + 35, task_area_y + 70), "Medium Priority", fill=(31, 41, 55), font=small_font)
        
        # Right side text content
        text_x = 420
        
        # Main title
        draw.text((text_x, 80), "MonthlyToDo", fill=(255, 255, 255), font=title_font)
        
        # Subtitle
        draw.text((text_x, 130), "Smart Task & Calendar Manager", fill=(255, 255, 255), font=subtitle_font)
        
        # Feature list
        features = [
            "Interactive Calendar Views",
            "Rich Task Management",
            "Achievement System", 
            "Progress Analytics",
            "Beautiful Material Design"
        ]
        
        for i, feature in enumerate(features):
            y_pos = 180 + i * 35
            # Feature bullet
            draw.ellipse([(text_x - 15, y_pos - 5), (text_x - 5, y_pos + 5)], fill=(255, 255, 255))
            # Feature text
            draw.text((text_x, y_pos), feature, fill=(255, 255, 255), font=feature_font)
        
        # Call to action
        draw.text((text_x, 360), "Transform Your Productivity", fill=(255, 255, 255), font=subtitle_font)
        
        # Google Play badge mockup
        badge_x, badge_y = text_x, 400
        draw.rounded_rectangle(
            [(badge_x, badge_y), (badge_x + 250, badge_y + 60)],
            radius=30,
            fill=(255, 255, 255, 80)
        )
        draw.text((badge_x + 125, badge_y + 30), "Available on Google Play", 
                 fill=(255, 255, 255), font=feature_font, anchor="mm")
        
        return img
    
    def main():
        print("🎨 Generating MonthlyToDo Feature Graphic...")
        
        # Create the feature graphic
        img = create_feature_graphic()
        
        # Save in the screenshots directory
        output_path = "MonthlyToDo_Feature_Graphic.png"
        img.save(output_path, "PNG", optimize=True)
        
        # Check file size
        file_size = os.path.getsize(output_path)
        file_size_mb = file_size / (1024 * 1024)
        
        print(f"✅ Feature Graphic generated successfully!")
        print(f"📁 File: {output_path}")
        print(f"📏 Dimensions: 1024x500px")
        print(f"💾 Size: {file_size_mb:.2f} MB")
        print(f"🎯 Google Play compliant: {'✅ Yes' if file_size_mb < 15 else '❌ No'}")
        
        return output_path
    
    if __name__ == "__main__":
        main()

except ImportError as e:
    print("❌ Error: PIL (Pillow) library not found.")
    print("💡 Please install it with: pip install Pillow")
    print(f"📋 Error details: {e}")
except Exception as e:
    print(f"❌ Error generating feature graphic: {e}")
    print("💡 Please check if all dependencies are installed correctly.") 