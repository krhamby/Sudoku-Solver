public class Arc {
    private int[] XiLocation;
    private int[] XjLocation;    

    public Arc(int XiRow, int XiColumn, int XjRow, int XjColumn) {
        this.XiLocation = new int[]{XiRow, XiColumn};
        this.XjLocation = new int[]{XjRow, XjColumn};
    }

    public int[] getXiLocation() {
        return XiLocation;
    }

    public int[] getXjLocation() {
        return XjLocation;
    }
}
