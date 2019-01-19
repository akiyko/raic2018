public final class MyStrategy extends StrategyWrapper implements Strategy {
    public MyStrategy() {
        super(new FinalStrategy(true));
    }
}
