 drinkDAO.addDrinkWithImage(getContext(), drink, selectedImageUri,
    aVoid -> {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                Log.d(TAG, "Drink saved successfully with image");
                showToast("Thêm đồ uống thành công");
            });
        }
    },
    e -> {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                Log.e(TAG, "Error saving drink with image", e);
                String errorMessage = "Lỗi khi thêm đồ uống";
                if (e.getMessage() != null) {
                    if (e.getMessage().contains("webp")) {
                        errorMessage = "Định dạng ảnh WebP không được hỗ trợ. Vui lòng chọn ảnh khác.";
                    } else {
                        errorMessage += ": " + e.getMessage();
                    }
                }
                showToast(errorMessage);
            });
        }
    });