package com.pixelstudio.listings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BooksAdapter extends ArrayAdapter<BooksData> {

    public BooksAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        BooksData book = getItem(position);

        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(
                    R.layout.result_view, parent, false);
        }

        TextView title = view.findViewById(R.id.title);
        TextView author = view.findViewById(R.id.author);
        title.setText(book.getTitle());
        author.setText(book.getAuthor());

        return view;
    }
}

