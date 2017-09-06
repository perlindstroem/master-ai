#include <iostream>
#include <array>

using namespace std;

void aprint(array<int, 3> arr)
{
    
    for(const int &elem: arr )
    {
        cout << elem;
    }
    cout << endl;
}

int main()
{
    // 1
    array<int, 3> one {1, 2, 3};
    
    aprint(one);

    return 0;
}