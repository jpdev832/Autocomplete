package com.otaliastudios.autocomplete;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Simple {@link AutocompletePresenter} implementation that hosts a {@link RecyclerView}.
 * Supports {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT} natively.
 * The only contract is to call {@link #dispatchClick(Object)} when an object is clicked.
 *
 * @param <T> your model object (the object displayed by the list)
 */
public abstract class RecyclerViewPresenter<T> extends AutocompletePresenter<T> {

    private RecyclerView recycler;
    private ClickProvider<T> clicks;
    private Observer observer;

    public RecyclerViewPresenter(Context context) {
        super(context);
    }

    @Override
    protected final void registerClickProvider(ClickProvider<T> provider) {
        this.clicks = provider;
    }

    @Override
    protected final void registerDataSetObserver(DataSetObserver observer) {
        this.observer = new Observer(observer);
    }

    @CallSuper
    @Override
    protected ViewGroup getView() {
        recycler = new RecyclerView(getContext());
        RecyclerView.Adapter adapter = instantiateAdapter();
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(instantiateLayoutManager());
        if (observer != null) {
            adapter.registerAdapterDataObserver(observer);
            observer = null;
        }
        return recycler;
    }

    @Override
    protected void onViewShown() {}

    @CallSuper
    @Override
    protected void onViewHidden() {
        recycler = null;
        observer = null;
    }

    @Nullable
    protected final RecyclerView getRecyclerView() {
        return recycler;
    }

    /**
     * Dispatch click event to {@link AutocompleteCallback}.
     * Should be called when items are clicked.
     *
     * @param item the clicked item.
     */
    protected final void dispatchClick(T item) {
        if (clicks != null) clicks.click(item);
    }

    /**
     * Provide an adapter for the recycler.
     * This should be a fresh instance every time this is called.
     *
     * @return a new adapter.
     */
    protected abstract RecyclerView.Adapter instantiateAdapter();

    /**
     * Provides a layout manager for the recycler.
     * This should be a fresh instance every time this is called.
     *
     * @return a new layout manager.
     */
    protected RecyclerView.LayoutManager instantiateLayoutManager() {
        return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    };

    private final static class Observer extends RecyclerView.AdapterDataObserver {

        private DataSetObserver root;

        Observer(DataSetObserver root) {
            this.root = root;
        }

        @Override
        public void onChanged() {
            root.onChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            root.onChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            root.onChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            root.onChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            root.onChanged();
        }
    }
}