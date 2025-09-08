#!/bin/bash

# This script tests the user API endpoints.
# It requires curl and jq to be installed.

HOST="http://localhost:8080"

echo "--- Running API Tests ---"

# 1. Create a new user
echo "\n[1] Testing: POST /users (Create User)"
CREATE_RESPONSE=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Doe","email":"jane.doe@example.com"}' \
  $HOST/users)

USER_ID=$(echo $CREATE_RESPONSE | jq '.id')

if [ -z "$USER_ID" ] || [ "$USER_ID" == "null" ]; then
  echo "  [FAIL] Could not create user or parse ID. Response: $CREATE_RESPONSE"
  exit 1
fi
echo "  [SUCCESS] User created with ID: $USER_ID"

# 2. Get the created user by ID
echo "\n[2] Testing: GET /users/:id (Get User by ID)"
curl -s $HOST/users/$USER_ID | jq

# 3. Update the user's email
echo "\n[3] Testing: PUT /users/:id/email (Update Email)"
curl -s -X PUT \
  -H "Content-Type: application/json" \
  -d '{"email":"jane.doe.updated@example.com"}' \
  $HOST/users/$USER_ID/email | jq

# 4. Get all users to see the change
echo "\n[4] Testing: GET /users (Get All Users)"
curl -s $HOST/users | jq

# 5. Try to create a duplicate user
echo "\n[5] Testing: POST /users (Create Duplicate User)"
DUPLICATE_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Doe","email":"jane.doe.updated@example.com"}' \
  $HOST/users)

if [ "$DUPLICATE_STATUS" -eq 409 ]; then
  echo "  [SUCCESS] Duplicate user creation failed as expected. Status code: $DUPLICATE_STATUS"
else
  echo "  [FAIL] Duplicate user creation did not return 409. Status code: $DUPLICATE_STATUS"
fi

# 6. Delete the user
echo "\n[6] Testing: DELETE /users/:id (Delete User)"
DELETE_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE $HOST/users/$USER_ID)
if [ "$DELETE_STATUS" -eq 204 ]; then
  echo "  [SUCCESS] User deleted. Status code: $DELETE_STATUS"
else
  echo "  [FAIL] Failed to delete user. Status code: $DELETE_STATUS"
fi

# 7. Verify the user is deleted
echo "\n[7] Testing: GET /users/:id (Verify Deletion)"
GET_STATUS=$(curl -s -o /dev/null -w "%{http_code}" $HOST/users/$USER_ID)
if [ "$GET_STATUS" -eq 404 ]; then
  echo "  [SUCCESS] User not found after deletion. Status code: $GET_STATUS"
else
  echo "  [FAIL] User still found after deletion. Status code: $GET_STATUS"
fi


# 8. Try to update a non-existent user
echo "\n[8] Testing: PUT /users/:id/email (Update Non-Existent User)"
UPDATE_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X PUT \
  -H "Content-Type: application/json" \
  -d '{"email":"jane.doe.updated@example.com"}' \
  $HOST/users/999/email)

if [ "$UPDATE_STATUS" -eq 404 ]; then
  echo "  [SUCCESS] Non-existent user update failed as expected. Status code: $UPDATE_STATUS"
else
  echo "  [FAIL] Non-existent user update did not return 404. Status code: $UPDATE_STATUS"
fi

# 9. Try to delete a non-existent user
echo "\n[9] Testing: DELETE /users/:id (Delete Non-Existent User)"
DELETE_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE $HOST/users/999)
if [ "$DELETE_STATUS" -eq 404 ]; then
  echo "  [SUCCESS] Non-existent user deletion failed as expected. Status code: $DELETE_STATUS"
else
  echo "  [FAIL] Non-existent user deleted successfully. Status code: $DELETE_STATUS"
fi

# 10. Try to get a non-existent user
echo "\n[10] Testing: GET /users/:id (Get Non-Existent User)"
GET_STATUS=$(curl -s -o /dev/null -w "%{http_code}" $HOST/users/999)
if [ "$GET_STATUS" -eq 404 ]; then
  echo "  [SUCCESS] Non-existent user retrieval failed as expected. Status code: $GET_STATUS"
else
  echo "  [FAIL] Non-existent user retrieved successfully. Status code: $GET_STATUS"
fi  

echo "\n--- Tests Complete ---"
