# Bài tập lớn Android

Nhóm 04
Lượt thuyết trình 7 buổi 15/05
3 14 15 7 5 9 4 (15/05) | 2 6 8 10 11 12 13 16 (23/05)
## Thành viên

| Họ và tên          | Mã sinh viên | Phân công                                                     |
| :----------------- | :----------: | :------------------------------------------------------------ |
| Trần Việt Dũng     |  B21DCCN036  | Nghiên cứu kết nối dữ liệu AtlasMongo và Imagekit Dao + Model |
| Trần Đức Lộc       |  B21DCCN492  | Trang chi tiết đồ uống, trang tìm kiếm có filter              |
| Nguyễn Trần Đạt    |  B21DCCN216  | Trang chủ, trang favorite, trang profile, Navigation, Login, Signup, Welcoming        |
| Đặng Thị Hồng Ngát |  B21DCCN564  | Báo cáo + slide, trang discover                               |
| Nguyễn Quang Hà    |  B21DCCN312  | Chức năng tạo công thức mới (Logic + giao diện)               |

Hẹn 21 thứ 2 12/05/2025

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
  - uuid
  - Tên đồ uống
  - id user
  - Image
  - id Category
  - Instruction
  - Description
  - Rate (mặc định)
- Model Recipe
  - uuid
  - id đồ uống
  - id Nguyên liệu
  - Liều lượng/số lượng
- Model Nguyên liệu (Ingredients) - Mặc định
  - uuid
  - Tên nguyên liệu
  - Description
  - Image
  - Đơn vị
  
- Model user
  - uuid
  - saveUuidFromAuthen
  - name
  - email
  - password
  - Image
- Model Category - Mặc định
  - uuid
  - name
  - description
  - image
- Model Review
  - uuid
  - id user
  - id đồ uống
  - comment
  - rate
- Model Favorite
  - uuid
  - id user
  - id đồ uống

## Các đầu mục công việc
- Nghiên cứu kết nối dữ liệu FireStore và Imagur, Dao + Model, các trang quản lý db (admin) (Dũng) (Package data)
- Chức năng đăng kí, đăng nhập bằng Email, password (Package Login) (ĐạtĐạt)
- Chức năng tạo công thức mới (Logic + giao diện) (Hà) (Activity) (Package CreateDrink)
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

- Trang chủ, trang profile, Navigation (Đạt) (Package Main)

- Trang chi tiết đồ uống, trang tìm kiếm có filter (Lộc) 
  - Trang chi tiết đồ uống (Activity) (Package DetailDrink)
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
  - Trang tìm kiếm (Activity) (Package Search)
    - Thanh tìm kiếm tìm tực thì khi gõ.
    - filter
      - category : checkbox + recycle view
      - ingredients: dropdown
    - Logic tìm kiếm
      - 1. Nếu có Category /& Name
          - Nếu có Filter Ingredient
              -> gọi DrinkDao -> List<Drink> a
              -> gọi RecipeDao -> List<Recipe> b  -> Trích drinkId
              -> For tìm giao a, b
          - Nếu không có
              -> gọi DrinkDao -> List<Drink> a
      - 2. Nếu không có Category / Name
          - Nếu có Filter Ingredient
              -> gọi RecipeDao -> List<Recipe> b  -> Trích drinkId
              -> Gọi DrinkDao.getByListDrinkId
          - Nếu không có
              -> gọi DrinkDao.getAllLimit -> List<Drink> a




- Trang discover, trang favorite, (Ngát) (Fragment) (Package Discover)
  - Discovery:
    - Category: Recycle view
    - Ingredient: Recycle view
    - Khi ấn gửi Intent chứa nội dung 
          "idCategory = xyz, idIngredient = null" đi đén Trang tìm kiếm.
  - Favourite:
    - Danh sách favourite
      - RecyclerView, Vertical + GridLayoutManager
      - Item: Tên, ảnh, nút trái tim (click -> thêm/xóa favourite)
    - Danh sách gợi ý
      - ...
- Báo cáo (Ngát)

## Các công nghệ áp dụng
- Công cụ: Android studio
- Ngôn ngữ: Java
- UI framework: 
  - XML: xây dựng giao diện
  - View Binding
- Architecture: MVP (Model - View - Presenter)
- Database: 
  - Firebase Authentication: Đăng nhập và xác thực người dùng.
  - Firebase FireStore Database + Imgur
  - SharedPreferences (lưu cài đặt người dùng cục bộ)
- Các thư viện hỗ trợ:
  - Retrofit: gọi API ngoài
  - Glide: Thư viện load và caching ảnh từ link
## Hướng dẫn cài đặt

Tài khoản sample:
- Email: datforitwork@gmail.com
- Password: D@tb21dccn216
