import java.util.Arrays;
class reverse
{
    public static void main(String[] args) {
        String str="Hello";
        Character[] chararr=new Character[str.length()];
        for(int i=0;i<str.length();i++)
        {
            chararr[i]=str.charAt(i);
        }
        System.out.println("Before reverse"+Arrays.toString(chararr));
        revArray(chararr);
        System.err.println("After reverse"+Arrays.toString(chararr));
        StringBuilder reversed=new StringBuilder();
        for(Character ch : chararr)
        {
            reversed.append(ch);
        }
        System.out.println("reversed  string"+reversed.toString());
    }
    public static <T> void revArray(T[] arr)
    {
        int left=0,right;
        right = arr.length -1;
        while(left<right)
        {
            T temp=arr[left];
            arr[left]=arr[right];
            arr[right]=temp;
            left++;
            right--;
        }
    }
}