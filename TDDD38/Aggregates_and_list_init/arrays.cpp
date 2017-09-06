#include <iostream>
#include <array>
#include <initializer_list>

using namespace std;

struct S
{
    int a[4] {3,4,5,6};
};

void aprint(array<int, 3> arr)
{
    for(const int &elem: arr )
    {
        cout << elem;
    }
    cout << endl;
}

template<typename T, int N>
void print_array(T (&a)[N])  // a is reference to array with type int[N]
{
   for (auto x : a)
      cout << x << ' ';
   cout << '\n';
}

void init_list(initializer_list<int> list)
{
    for(auto el : list)
    {
        cout << el;
    }
    cout << endl;
}

initializer_list<int> ret_init_list() {
    return {2, 3, 4};
}

int main()
{
    // 1
    array<int, 3> one {1, 2, 3};
    int two[] {4, 5, 6};
    int* three = new int[3]{6,7,8};
    
    aprint(one);
    print_array(two);

    for(auto elem = three; elem != three + 3; ++elem)
    {
        cout << *elem;
    }
    cout << endl;

    for(int elem : two)
    {
        cout << elem;
    }
    cout << endl;

    //function argument
    init_list({1,2,3});

    //return argument
    for(auto el : ret_init_list())
    {
        cout << el;
    }
    cout << endl;

    S s;

    print_array(s.a);


    //subscript
    int sub[2];
    sub[0] = 1, sub[1] = 3;

    print_array(sub);

    return 0;
}