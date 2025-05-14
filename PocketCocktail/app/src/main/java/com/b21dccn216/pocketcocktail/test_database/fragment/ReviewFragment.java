package com.b21dccn216.pocketcocktail.test_database.fragment;

import android.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.dao.ReviewDAO;
import com.b21dccn216.pocketcocktail.dao.UserDAO;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Review;
import com.b21dccn216.pocketcocktail.model.User;
import com.b21dccn216.pocketcocktail.test_database.adapter.ReviewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReviewFragment extends BaseModelFragment {
    private EditText etUuid, etCreatedAt, etUpdatedAt, etComment, etRate;
    private Spinner spinnerDrink, spinnerUser;
    private Button btnSave, btnUpdate, btnDelete;
    private ListView lvReviews;
    private ReviewAdapter adapter;
    private List<Review> reviews;
    private Review selectedReview;
    private ReviewDAO reviewDAO;
    private DrinkDAO drinkDAO;
    private UserDAO userDAO;
    private SimpleDateFormat dateFormat;
    private List<Drink> drinks;
    private List<User> users;

    @Override
    protected int getLayoutId() {
        return R.layout.test_database_fragment_review;
    }

    @Override
    protected void initViews() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        etUuid = rootView.findViewById(R.id.etUuid);
        etCreatedAt = rootView.findViewById(R.id.etCreatedAt);
        etUpdatedAt = rootView.findViewById(R.id.etUpdatedAt);
        etComment = rootView.findViewById(R.id.etComment);
        etRate = rootView.findViewById(R.id.etRate);
        spinnerDrink = rootView.findViewById(R.id.spinnerDrink);
        spinnerUser = rootView.findViewById(R.id.spinnerUser);

        btnSave = rootView.findViewById(R.id.btnSave);
        btnUpdate = rootView.findViewById(R.id.btnUpdate);
        btnDelete = rootView.findViewById(R.id.btnDelete);
        lvReviews = rootView.findViewById(R.id.lvReviews);

        reviews = new ArrayList<>();
        drinks = new ArrayList<>();
        users = new ArrayList<>();

        adapter = new ReviewAdapter(getContext(), reviews);
        lvReviews.setAdapter(adapter);

        reviewDAO = new ReviewDAO();
        drinkDAO = new DrinkDAO();
        userDAO = new UserDAO();

        setupSpinners();
        setupListeners();
        loadData();
    }

    private void setupSpinners() {
        // Setup Drink spinner
        List<String> drinkItems = new ArrayList<>();
        drinkItems.add(""); // Add empty option
        ArrayAdapter<String> drinkAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                drinkItems
        );
        drinkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDrink.setAdapter(drinkAdapter);

        // Setup User spinner
        List<String> userItems = new ArrayList<>();
        userItems.add(""); // Add empty option
        ArrayAdapter<String> userAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                userItems
        );
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUser.setAdapter(userAdapter);

        // Load drinks and users
        loadDrinks();
        loadUsers();
    }

    private void loadDrinks() {
        drinkDAO.getAllDrinks(new DrinkDAO.DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinkList) {
                drinkList.sort((c1, c2) -> c2.getUpdatedAt().compareTo(c1.getUpdatedAt()));
                drinks.clear();
                drinks.addAll(drinkList);

                List<String> drinkItems = new ArrayList<>();
                drinkItems.add(""); // Add empty option
                for (Drink drink : drinks) {
                    drinkItems.add(drink.getName() + " (" + drink.getUuid() + ")");
                }

                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerDrink.getAdapter();
                adapter.clear();
                adapter.addAll(drinkItems);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                showToast("Error loading drinks: " + e.getMessage());
            }
        });
    }

    private void loadUsers() {
        userDAO.getAllUsers(new UserDAO.UserListCallback() {
            @Override
            public void onUserListLoaded(List<User> userList) {
                userList.sort((c1, c2) -> c2.getUpdatedAt().compareTo(c1.getUpdatedAt()));
                users.clear();
                users.addAll(userList);

                List<String> userItems = new ArrayList<>();
                userItems.add(""); // Add empty option
                for (User user : users) {
                    userItems.add(user.getName() + " (" + user.getUuid() + ")");
                }

                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerUser.getAdapter();
                adapter.clear();
                adapter.addAll(userItems);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                showToast("Error loading users: " + e.getMessage());
            }
        });
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveItem());
        btnUpdate.setOnClickListener(v -> updateItem());
        btnDelete.setOnClickListener(v -> deleteItem());

        lvReviews.setOnItemClickListener((parent, view, position, id) -> {
            selectedReview = reviews.get(position);
            fillInputs(selectedReview);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true); 
        });
    }

    @Override
    protected void loadData() {
        reviewDAO.getAllReviews(new ReviewDAO.ReviewListCallback() {
            @Override
            public void onReviewListLoaded(List<Review> reviewList) {
                reviewList.sort((c1, c2) -> c2.getUpdatedAt().compareTo(c1.getUpdatedAt()));
                reviews.clear();
                reviews.addAll(reviewList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                showToast("Error loading reviews: " + e.getMessage());
            }
        });
    }

    @Override
    protected void clearInputs() {
        etUuid.setText("");
        etCreatedAt.setText("");
        etUpdatedAt.setText("");
        etComment.setText("");
        etRate.setText("");
        spinnerDrink.setSelection(0);
        spinnerUser.setSelection(0);
        selectedReview = null;
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    @Override
    protected void fillInputs(Object item) {
        if (item instanceof Review) {
            Review review = (Review) item;
            etUuid.setText(review.getUuid());
            etCreatedAt.setText(dateFormat.format(review.getCreatedAt()));
            etUpdatedAt.setText(dateFormat.format(review.getUpdatedAt()));
            etComment.setText(review.getComment());
            etRate.setText(String.valueOf(review.getRate()));

            // Set drink spinner
            String drinkId = review.getDrinkId();
            boolean drinkFound = false;
            for (int i = 0; i < drinks.size(); i++) {
                if (drinks.get(i).getUuid().equals(drinkId)) {
                    spinnerDrink.setSelection(i + 1); // +1 because of empty option
                    drinkFound = true;
                    break;
                }
            }
            if (!drinkFound) {
                spinnerDrink.setSelection(0);
            }

            // Set user spinner
            String userId = review.getUserId();
            boolean userFound = false;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUuid().equals(userId)) {
                    spinnerUser.setSelection(i + 1); // +1 because of empty option
                    userFound = true;
                    break;
                }
            }
            if (!userFound) {
                spinnerUser.setSelection(0);
            }
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        btnSave.setEnabled(enabled);
        btnUpdate.setEnabled(enabled && selectedReview != null);
        btnDelete.setEnabled(enabled && selectedReview != null);
    }

    @Override
    protected void saveItem() {
        int drinkPosition = spinnerDrink.getSelectedItemPosition();
        int userPosition = spinnerUser.getSelectedItemPosition();
        String comment = etComment.getText().toString();
        String rateStr = etRate.getText().toString();

        if (drinkPosition <= 0 || userPosition <= 0 || comment.isEmpty() || rateStr.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin");
            return;
        }

        double rate;
        try {
            rate = Double.parseDouble(rateStr);
            if (rate < 0 || rate > 5) {
                showToast("Đánh giá phải từ 0 đến 5");
                return;
            }
        } catch (NumberFormatException e) {
            showToast("Giá trị đánh giá không hợp lệ");
            return;
        }

        String drinkId = drinks.get(drinkPosition - 1).getUuid();
        String userId = users.get(userPosition - 1).getUuid();

        // Check if review already exists
        boolean reviewExists = false;
        for (Review review : reviews) {
            if (review.getDrinkId().equals(drinkId) && review.getUserId().equals(userId)) {
                reviewExists = true;
                break;
            }
        }

        if (reviewExists) {
            new AlertDialog.Builder(requireContext())
                .setTitle("Cảnh báo")
                .setMessage("Đã tồn tại đánh giá của người dùng này cho đồ uống này. Bạn có chắc chắn muốn tạo mới?")
                .setPositiveButton("Có", (dialog, which) -> {
                    performSave(drinkId, userId, comment, rate);
                })
                .setNegativeButton("Không", null)
                .show();
        } else {
            performSave(drinkId, userId, comment, rate);
        }
    }

    private void performSave(String drinkId, String userId, String comment, double rate) {
        setButtonsEnabled(false);
        Review review = new Review(drinkId, userId, comment, rate);
        review.generateUUID();

        reviewDAO.addReview(review,
                aVoid -> {
                    showToast("Thêm đánh giá thành công");
                    clearInputs();
                    loadData();
                    setButtonsEnabled(true);
                },
                e -> {
                    showToast(e.getMessage());
                    setButtonsEnabled(true);
                });
    }

    @Override
    protected void updateItem() {
        if (selectedReview == null) {
            showToast("Vui lòng chọn một đánh giá");
            return;
        }

        int drinkPosition = spinnerDrink.getSelectedItemPosition();
        int userPosition = spinnerUser.getSelectedItemPosition();
        String comment = etComment.getText().toString();
        String rateStr = etRate.getText().toString();

        if (drinkPosition <= 0 || userPosition <= 0 || comment.isEmpty() || rateStr.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin");
            return;
        }

        double rate;
        try {
            rate = Double.parseDouble(rateStr);
            if (rate < 0 || rate > 5) {
                showToast("Đánh giá phải từ 0 đến 5");
                return;
            }
        } catch (NumberFormatException e) {
            showToast("Giá trị đánh giá không hợp lệ");
            return;
        }

        setButtonsEnabled(false);
        String drinkId = drinks.get(drinkPosition - 1).getUuid();
        String userId = users.get(userPosition - 1).getUuid();

        selectedReview.setDrinkId(drinkId);
        selectedReview.setUserId(userId);
        selectedReview.setComment(comment);
        selectedReview.setRate(rate);

        reviewDAO.updateReview(selectedReview,
                aVoid -> {
                    showToast("Cập nhật đánh giá thành công");
                    clearInputs();
                    loadData();
                    setButtonsEnabled(true);
                },
                e -> {
                    showToast(e.getMessage());
                    setButtonsEnabled(true);
                });
    }

    @Override
    protected void deleteItem() {
        if (selectedReview == null) {
            showToast("Vui lòng chọn một đánh giá");
            return;
        }

        new AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa đánh giá này?")
            .setPositiveButton("Có", (dialog, which) -> {
                performDelete();
            })
            .setNegativeButton("Không", null)
            .show();
    }

    private void performDelete() {
        setButtonsEnabled(false);
        reviewDAO.deleteReview(selectedReview.getUuid(),
                aVoid -> {
                    showToast("Xóa đánh giá thành công");
                    clearInputs();
                    loadData();
                    setButtonsEnabled(true);
                },
                e -> {
                    showToast(e.getMessage());
                    setButtonsEnabled(true);
                });
    }
} 