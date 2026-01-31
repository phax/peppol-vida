#!/usr/bin/env python3
import sys
import uuid

def main():
    if len(sys.argv) != 3:
        print("Usage: uuidv5.py <namespace-uuid> <name>", file=sys.stderr)
        sys.exit(1)

    namespace_uuid = sys.argv[1]
    name = sys.argv[2]

    try:
        ns = uuid.UUID(namespace_uuid)
    except ValueError:
        print("Invalid namespace UUID", file=sys.stderr)
        sys.exit(2)

    result = uuid.uuid5(ns, name)
    print(result)

if __name__ == "__main__":
    main()
