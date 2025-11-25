package archivos;

public class interm {
    public static void main(String[] args) {
        for(int i = 0; i < 2; i=i+1){
            int a = i*2,b = i*3,c = i*4,d = i*5;

            int Moment = a + b * d - c;

            double promedio = 0;
            int[] cals = {90,90,80,80,100,50};
            promedio = (double) (cals[0] + cals[1] + cals[2] + cals[3] + cals[4] + cals[5]) /6;
            for(int j = 2; j < cals.length; j=j+1){
                promedio = promedio+cals[i];
            }
            promedio = promedio / cals.length;
        }
    }
}
