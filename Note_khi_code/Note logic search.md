
1. Nếu có Category / Name
   1. Nếu có Filter Ingredient
      1. 




    -> gọi DrinkDao -> List<Drink> a-> gọi RecipeDao -> List<Recipe> b-> For tìm giao a, b
 Nếu không có -> gọi DrinkDao -> List<Drink> a
Nếu không có Category / Name
 Nếu có Filter Ingredient-> gọi RecipeDao -> List<Recipe> b  -> Trích drinkId-> Gọi DrinkDao.getByListDrinkId
 Nếu không có-> gọi DrinkDao.getAllLimit -> List<Drink> a