class GenericNumber
{
    public static void main(String[] args) {
        Integer[] numbers={10,20,30,40};
        boolean res1=check(numbers,10,20);
        boolean res2=check(numbers,30,10);
        System.out.println("both 10 and 20 exists"+res1);
        System.out.println("Both 30 and 10  exists"+res2);
    }
    public static <T> boolean check(T[] arr,T a,T b)
    {
        boolean foundA=false,foundb=false;
        for(T item : arr)
        {
            if(item.equals(a))
            {
                foundA=true;
            }
            if(item.equals((b)))
            {
                foundb=true;
            }
        }
        return foundA && foundb;
    }
}