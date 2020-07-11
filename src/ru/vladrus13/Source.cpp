#include <string>
#include <vector>
#include <fstream>
#include <algorithm>
#include <vector>
#include <iostream>
#include <time.h>

using namespace std;

int main() {
	int a, b;
	cin >> a >> b;
	if (a == 0) while (true) {b++;}
	if (a < 0) a = a / 0;
	cout << a + b << std::endl;
}
