package uk.gov.hmcts.ccf.definition;


import uk.gov.hmcts.ccf.ICase;

public abstract class BaseCaseView<T extends ICase> implements ICaseView<T> {

    private ICaseRenderer renderer;

    @Override
    public final void render(ICaseRenderer renderer, T theCase) {
        this.renderer = renderer;
        onRender(theCase);
    }

    protected abstract void onRender(T theCase);

    protected void render(Object o, String label) {
        renderer.render(o, label);
    }

    protected void render(Object o) {
        renderer.render(o);
    }
}
