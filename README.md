# Bài tập lớn Android

## Thành viên

| Họ và tên          | Mã sinh viên | Phân công                                                     |
| :----------------- | :----------: | :------------------------------------------------------------ |
| Trần Việt Dũng     |  B21DCCN036  | Nghiên cứu kết nối dữ liệu AtlasMongo và Imagekit Dao + Model |
| Trần Đức Lộc       |  B21DCCN492  | Trang chi tiết đồ uống, trang tìm kiếm có filter              |
| Nguyễn Trần Đạt    |  B21DCCN216  | Trang chủ, trang favorite, trang profile, Navigation          |
| Đặng Thị Hồng Ngát |  B21DCCN564  | Báo cáo + slide, trang discover                               |
| Nguyễn Quang Hà    |  B21DCCN312  | Chức năng tạo công thức mới (Logic + giao diện)               |

## Chủ đề

App tạo và chia sẻ công thức pha chế các loại đồ uống hấp dẫn như: Trà, Nước trái cây, Cocktail không cồn, Cocktail có cồn, Thức uống nạp năng lượng,...

## Các chức năng chính

- Đăng nhập bằng Firebase
- Trang chủ có các danh mục: Nổi bật, Trà, Nước trái cây, Cocktail không cồn, Cocktail có cồn, Thức uống nạp năng lượng,...
  - Thanh tìm kiếm, filter.
  - Add button
- Trang discover
  - Category, Ingredients
  - Ấn vô 1 category/Ingredients -> Danh sách gồm tất cả các loại đồ uống tương ứng (  - Thanh tìm kiếm, filter.)
- Trang Favorite
- Trang cài đặt tài khoản
  - Thay đổi thông tin cá nhân
  - Đăng xuất

## Thiết kế model

- Model Drink đồ uống:
  - id
  - Tên đồ uống
  - id user
  - Image
  - id Category
  - Instruction
  - Description
  - Rate (mặc định)
- Model Recipe
  - id đồ uống
  - id Nguyên liệu
  - Liều lượng/số lượng
- Model Nguyên liệu (Ingredients) - Mặc định
  - id
  - Tên nguyên liệu
  - Description
  - Image
  - Đơn vị
  
- Model user
  - id
  - name
  - email
  - password
  - Image
- Model Category - Mặc định
  - id
  - name
- Model Review
  - id
  - id user
  - id đồ uống
  - comment
  - rate

## Các đầu mục công việc
- Nghiên cứu kết nối dữ liệu AtlasMongo và Imagekit Dao + Model (Dũng)
~~- Chức năng đăng kí, đăng nhập bằng Email, password~~
- Chức năng tạo công thức mới (Logic + giao diện) (Hà)
  - Ô nhập tên đồ uống
  - Thêm ảnh đồ uống
  - Ô nhập String hướng dẫn các bước pha chế
  - Rate (0 - 5 star) giống cô hướng dẫn
  - Ô nhập Description
  - Dropdown chọn Category
  - Recycle view hiển thị danh sách nguyên liệu
    - Nút thêm nguyên liệu
    - Dropdown chọn nguyên liệu (có ảnh nguyên liệu sẵn và tên)
    - Số lượng, liều lượng (có đơn vị ở cạnh)

- Trang chủ, trang favorite, trang profile, Navigation (Đạt)

- Trang chi tiết đồ uống, trang tìm kiếm có filter (Lộc)
  - Trang chi tiết đồ uống
    - Nút favorite
    - Nút Chia sẻ (Optional)
    - Tên
    - name Tác giả
    - Category
    - Instruction
    - Description
    - Rate (mặc định)
    - Review list
    - Ô review + Rate (0 - 5 star) giống cô hướng dẫn (Option)
  - Trang tìm kiếm
    - Thanh tìm kiếm tìm tực thì khi gõ.
    - filter
      - category : checkbox + recycle view
      - ingredients: dropdown
- Báo cáo + slide, trang discover (Ngát)
  - Category: Recycle view
  - Ingredient: Recycle view
  - Khi ấn gửi Intent chứa nội dung 
        "idCategory = xyz, idIngredient = null" đi đén Trang tìm kiếm.

## Các công nghệ áp dụng

## Hướng dẫn cài đặt
