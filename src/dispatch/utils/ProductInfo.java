package dispatch.utils;

/**
 * Created by myl
 * on 2015/2/11.
 */
public class ProductInfo {
    String psiCode;
    String psiDesc;
    String year;
    String dispatchNum;

    public String getDispatchNum() {
        return dispatchNum;
    }

    public void setDispatchNum(String dispatchNum) {
        this.dispatchNum = dispatchNum;
    }

    public String getPsiCode() {
        return psiCode;
    }

    public void setPsiCode(String psiCode) {
        this.psiCode = psiCode;
    }

    public String getPsiDesc() {
        return psiDesc;
    }

    public void setPsiDesc(String psiDesc) {
        this.psiDesc = psiDesc;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
