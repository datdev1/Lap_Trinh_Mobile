Tôi muốn tìm kiếm Drink theo 3 điều kiện:
1. Name Drink có chứa string trong ô tìm kiếm (searchString) / 1 chuỗi trống
2. Có categoryID (nullable) (khi không chọn thì trả về tất cả)
3. Có chứa đủ list IngredientID (Được lưu trong Recipe với dạng drinkID và ingredientID) (khi không chọn thì trả về tất cả)
Bạn hãy viết cho tôi hàm truy vấn cho DrinkDAO và RecipeDAO để xử lý.
Xử lý cả nếu nó trống/null. Hãy xử lý sao cho tối ưu nhất vì có thể drink là rất nhiều, còn Ingredient thì vừa phải.
Đây là hướng tớ nghĩ ra:
Viết hàm public void searchDrinksByCategoryAndIngredientID(String query, @Nullable String categoryId, DrinkListCallback callback) bên trong xử lý 4 trường hợp

1. Nếu có Category / Name và có list IngredientID
   - Bước 1 Đầu tiên gọi tới hàm public void searchDrinksByCategory(String query, @Nullable String categoryId, DrinkListCallback callback) nhận về được list<Drink>  thỏa mãn 2 điều kiện SearchString và CategoryID
   - Bước 2 Lấy list<String> DrinkID từ list<Drink> dùng hàm của RecipeDAO: public void searchDrinkIDByIngredient(list<String> DrinkID, list<String> IngredientID, DrinkIDListCallback callback)
   - Bước 3 Nhận về list<String> DrinkID mới thì lọc lại list<Drink> ban đầu, lấy ra các Drink thỏa mã cả 3 điều kiện lọc
   - Bước 4 Sort lại theo name Drink
2. Nếu có Category / Name và không có list IngredientID
   - Bước 1 Đầu tiên gọi tới hàm public void searchDrinksByCategory(String query, @Nullable String categoryId, DrinkListCallback callback) nhận về được list<Drink>  thỏa mãn 2 điều kiện SearchString và CategoryID
   - Bước 2 Sort lại theo name Drink
3. Nếu không có Category / Name và có list IngredientID
   - Bước 1 Gọi tới RecipeDAO trước public void searchDrinkIDByIngredientNoListDrinkID(list<String> IngredientID, DrinkIDListCallback callback)
   - Bước 2 Trả về 1 list<String> DrinkID 
   - Bước 3 Bên DrinkDAO gọi hàm getAllDrinkWithListDrinkID (list<String> DrinkID, DrinkListCallback callback)
   - Bước 4 Sort lại theo name Drink
4. Nếu không có Category / Name và không có list IngredientID
   - Bước 1Gọi getAllDrinkWithLimit(int limit, DrinkListCallback callback) trong DrinkDAO


 Nếu có Filter Ingredient-> gọi RecipeDao -> List<Recipe> b  -> Trích drinkId-> Gọi DrinkDao.getByListDrinkId
 Nếu không có-> gọi DrinkDao.getAllLimit -> List<Drink> a