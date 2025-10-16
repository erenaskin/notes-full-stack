import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart'; 

import 'package:notes/main.dart';

void main() {
  // This file is intentionally left blank.
  // Widget tests will be added here as the application develops.
  testWidgets('Application starts successfully', (WidgetTester tester) async {
    await tester.pumpWidget(const ProviderScope(child: MyApp()));
    expect(find.byType(MyApp), findsOneWidget);
  });
}
