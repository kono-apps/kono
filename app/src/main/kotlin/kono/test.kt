package kono

import kono.export.ExportFunction

fun testK(a: Int = 10, b: Int = 20, c: Int = 30) {

}

fun TEST_A() {
    testK(a = 111)
}

fun TEST_B_A() {
    testK(a = 444, b = 333)
}

fun TEST_A_C() {
    testK(a = -93, c = 420)
}

fun TEST_B_C() {
    testK(b = 94, c = 48)
}

class Point()

typealias NotString = String
