package com.b21dccn216.pocketcocktail.view.Welcome.Fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.databinding.FragmentWelcomeDiscoveryBinding;
import com.b21dccn216.pocketcocktail.view.Welcome.Adapter.ImageRecyclerAdapter;


public class DiscoveryFragment extends Fragment {
    private FragmentWelcomeDiscoveryBinding binding;
    private ImageRecyclerAdapter adapter;
    private final int spanRow = 2;
    public DiscoveryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWelcomeDiscoveryBinding.inflate(getLayoutInflater());

        setUpRecyclerView();
        return binding.getRoot();
    }

    private void setUpRecyclerView() {
        // Item decoration to fit honey Comb
        RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, int position, @NonNull RecyclerView parent) {
                super.getItemOffsets(outRect, position, parent);

                int row = position % spanRow;

                int width = (int) (getActivity().getResources().getDimension(R.dimen.width)) ;

                if (row % 2 == 0) {
                    // top row
                     outRect.left = - width/2;
                }else{
//                    outRect.left = 100;
                }
                if(row  == 1) outRect.top = - (int) (width/4 + 20);

            }
        };

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), spanRow, GridLayoutManager.HORIZONTAL, false){
//            @Override
//            public boolean canScrollHorizontally() {
//                return false;
//            }
        };

        binding.recycler.setHasFixedSize(true);
        binding.recycler.setLayoutManager(gridLayoutManager);
        binding.recycler.addItemDecoration(itemDecoration);

        adapter = new ImageRecyclerAdapter();
        binding.recycler.setAdapter(new ImageRecyclerAdapter());
        binding.recycler.smoothScrollBy(100 ,0);
    }


}