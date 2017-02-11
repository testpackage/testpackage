## [1.0.0] - 2017-02-11
### Fixed
* Fix EmptyStackException upon error in test @Before/@After methods (#41)
* Fix error when test names don't fit in console width (#40)
* Exclude abstract test classes (#36)

### Changed
* Use extracted, improved Visible Assertions library ([Github](https://github.com/rnorth/visible-assertions)). **BREAKING CHANGE NOTE**: the package name for visible assertions classes is now `org.rnorth.visibleassertions`
